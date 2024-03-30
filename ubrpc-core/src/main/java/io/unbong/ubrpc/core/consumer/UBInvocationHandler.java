package io.unbong.ubrpc.core.consumer;

import io.unbong.ubrpc.core.api.*;
import io.unbong.ubrpc.core.consumer.http.OkHttpInvoker;
import io.unbong.ubrpc.core.meta.InstanceMeta;
import io.unbong.ubrpc.core.util.MethodUtil;
import io.unbong.ubrpc.core.util.TypeUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.SocketTimeoutException;
import java.util.List;

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
    List<InstanceMeta> _providers;

    HttpInvoker _httpInvoker;

    public UBInvocationHandler(Class<?> clazz, RpcContext context, List<InstanceMeta> providers){
        this.service = clazz;
        _context = context;
        _providers = providers;
        int timeout = Integer.valueOf(context.getParamaters().getOrDefault("app.timeout", "1000"));
        _httpInvoker = new OkHttpInvoker(timeout);
    }

    /**
     *   动态代理的代理类，
     * @param proxy the proxy instance that the method was invoked on
     *
     * @param method the {@code Method} instance corresponding to
     * the interface method invoked on the proxy instance.  The declaring
     * class of the {@code Method} object will be the interface that
     * the method was declared in, which may be a superinterface of the
     * proxy interface that the proxy class inherits the method through.
     *
     * @param args an array of objects containing the values of the
     * arguments passed in the method invocation on the proxy instance,
     * or {@code null} if interface method takes no arguments.
     * Arguments of primitive types are wrapped in instances of the
     * appropriate primitive wrapper class, such as
     * {@code java.lang.Integer} or {@code java.lang.Boolean}.
     *
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
        rpcRequest.setParameters(_context.getParamaters());

        int retries = Integer.valueOf( _context.getParamaters().getOrDefault("app.retries", "1"));


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

                        //return preResponce;
                    }
                }


                List<InstanceMeta> instances = _context.getRouter().route(_providers);
                InstanceMeta node = (InstanceMeta) _context.getLoadBalancer().choose(instances);
                log.debug("loadBalancer.choose(urls)==>  " + node.toURL());
                String url = node.toURL();
                RpcResponse<?> rpcResponse = _httpInvoker.post(rpcRequest, url);
                Object result = castReturnResult(method, rpcResponse);


                for (Filter filter : _context.getFilters()) {
                    result =filter.postFilter(rpcRequest, rpcResponse, result);
                }

                return result;
//        return null;
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

    @Nullable
    private static Object castReturnResult(Method method, RpcResponse<?> rpcResponse) {
        if(rpcResponse.isStatus()){

            Object rpcData =  rpcResponse.getData();

            return TypeUtils.castMethodReturnType(method, rpcData);
        }
        else{
            Exception ex = rpcResponse.getException();
            //ex.printStackTrace();
            if(ex instanceof RpcException exception)
            {
                throw exception;
            }

            throw new RpcException(ex, ErrorCode.UNKNOWN);
        }
    }
}
