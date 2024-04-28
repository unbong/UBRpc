package io.unbong.ubrpc.core.consumer;

import io.unbong.ubrpc.core.api.*;
import io.unbong.ubrpc.core.consumer.http.OkHttpInvoker;
import io.unbong.ubrpc.core.governance.SlidingTimeWindow;
import io.unbong.ubrpc.core.meta.InstanceMeta;
import io.unbong.ubrpc.core.util.MethodUtil;
import io.unbong.ubrpc.core.util.TypeUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * 消费端动态代理处理类
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-03-10 21:02
 */
@Slf4j
public class UBInvocationHandler implements InvocationHandler {


    final static MediaType JSONTYPE = MediaType.get("application/json; charset=utf-8");
    Class<?> service ;
    RpcContext _context;
    final List<InstanceMeta> _providers;
    List<InstanceMeta> _isolateProviders = new ArrayList<>();
    final List<InstanceMeta> _halfProviders = new ArrayList<>();
    HttpInvoker _httpInvoker;

    // 统计实例30秒内出现故障次数
    Map<String, SlidingTimeWindow> windows = new HashMap<>();

    ScheduledExecutorService _executor;

    public UBInvocationHandler(Class<?> clazz, RpcContext context, List<InstanceMeta> providers){
        this.service = clazz;
        _context = context;
        _providers = providers;
        int timeout = Integer.valueOf(context.getParameters().getOrDefault("consumer.timeout", "1000"));
        _httpInvoker = new OkHttpInvoker(timeout);
        _executor = Executors.newScheduledThreadPool(1);
        int halfOpenInitialDelay = Integer.parseInt(context.getParameters()
                .getOrDefault("consumer.halfOpenInitialDelay", "10000"));
        int halfOpenDelay = Integer.parseInt(context.getParameters()
                .getOrDefault("consumer.halfOpenDelay", "60000"));
        _executor.scheduleWithFixedDelay(this::halfOpen, halfOpenInitialDelay, halfOpenDelay, TimeUnit.MILLISECONDS);
    }

    private void halfOpen() {

        log.debug("---> half open isolatedProviders {}" ,_isolateProviders);
        _halfProviders.clear();
        _halfProviders.addAll(_isolateProviders);
    }

    /**
     *   动态代理的代理类，
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        // 过滤Object对象的方法不去调用
        if(MethodUtil.checkLocalMethod(method)){
            return null;
        }

        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setService(service.getCanonicalName());
        rpcRequest.setMethodSign(MethodUtil.methodSign(method));
        rpcRequest.setArgs(args);
        // rpcRequest.setParameters(_context.getParamaters());

        int retries = Integer.valueOf( _context.getParameters().getOrDefault("consumer.retries", "1"));
        int faultLimit = Integer.parseInt(_context.getParameters()
                .getOrDefault("consumer.faultLimit", "10"));
        while(retries-- > 0)
        {
            log.info("---> retries: " + retries );
            try {
                // filter
                for (Filter filter : _context.getFilters()) {
                    Object preResponce =filter.preFilter(rpcRequest);
                    if(preResponce != null)
                    {
                        log.debug(filter.getClass().getName() + "---> preFilter response: " + preResponce);

                        return preResponce;
                    }
                }

                InstanceMeta instance;
                synchronized (_halfProviders)
                {
                    if(_halfProviders.isEmpty()){
                        List<InstanceMeta> instances = _context.getRouter().route(_providers);
                        instance= (InstanceMeta) _context.getLoadBalancer().choose(instances);
                        log.debug("loadBalancer.choose(urls)==>  " + instance.toURL());
                    }
                    else{
                        instance = _halfProviders.remove(0);
                        log.debug("check alive instance ---> {}",instance);
                    }
                }

                String url = instance.toURL();
                Object result;
                RpcResponse<?> rpcResponse ;


                try{
                    rpcResponse = _httpInvoker.post(rpcRequest, url);
                    result = castReturnResult(method, rpcResponse);

                }
                catch (Exception exp)
                {
                    // 故障的规则统计和隔离
                    // 每一次异常，记录一次，统计30s的异常数

                    SlidingTimeWindow window = windows.get(url);
                    if(window == null)
                    {
                        window = new SlidingTimeWindow();
                        windows.put(url, window);
                    }

                    window.record(System.currentTimeMillis());
                    log.debug("instance {} in window with {*", url, window.getSum());
                    // 30s内发生了10次  就故障隔离
                    if(window.getSum() >= faultLimit)
                    {
                        isolate(instance);
                    }
                    throw exp;
                }

                synchronized (_providers)
                {
                    // 请求时探活请求时
                    if(!_providers.contains(instance)){
                        _isolateProviders.remove(instance);
                        _providers.add(instance);
                        log.debug("instance {} is recovered. isolatedProvider={}, providers={}", instance, _isolateProviders, _providers);
                    }
                }

                for (Filter filter : _context.getFilters()) {
                    Object filterResult =filter.postFilter(rpcRequest, rpcResponse, result);
                    if(filterResult != null){
                        return filterResult;
                    }
                }

                return result;
            }catch (Exception e)
            {
                e.printStackTrace();
                if (! (e.getCause() instanceof SocketTimeoutException))
                {
                    log.debug("socket timeout happen. retries:" + retries);
                    throw e;
                }
            }
        }

        return null ;
    }

    /**
     *
     * @param instance
     */
    private void isolate(InstanceMeta instance) {
        log.debug("---> isolate instance {}", instance);
        _providers.remove(instance);
        log.debug("---> providers ={}", _providers);
        _isolateProviders.add(instance);
        log.debug("---> isolatedProviders ={}", _isolateProviders);

    }

    @Nullable
    private static Object castReturnResult(Method method, RpcResponse<?> rpcResponse) {
        if(rpcResponse.isStatus()){

            Object rpcData =  rpcResponse.getData();

            return TypeUtils.castMethodReturnType(method, rpcData);
        }
        else{
            RpcException ex = rpcResponse.getException();
            //ex.printStackTrace();
            if(ex != null)
            {
                log.error("response error.", ex);
                throw ex;
            }

            return null;
        }
    }
}
