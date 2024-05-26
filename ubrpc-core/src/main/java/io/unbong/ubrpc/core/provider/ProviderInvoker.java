package io.unbong.ubrpc.core.provider;

import io.unbong.ubrpc.core.api.RpcContext;
import io.unbong.ubrpc.core.api.RpcException;
import io.unbong.ubrpc.core.api.RpcRequest;
import io.unbong.ubrpc.core.api.RpcResponse;
import io.unbong.ubrpc.core.config.ProviderConfigurationProperties;
import io.unbong.ubrpc.core.governance.SlidingTimeWindow;
import io.unbong.ubrpc.core.meta.ProviderMeta;
import io.unbong.ubrpc.core.util.TypeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 *  服务端的对于访问方法的功能
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-03-21 22:01
 */
@Slf4j
public class ProviderInvoker {

    private MultiValueMap<String, ProviderMeta> skeleton;

    private ProviderConfigurationProperties providerConfigurationProperties;

    public ProviderInvoker(ProviderBootStrap providerBootStrap) {
        this.skeleton = providerBootStrap.getSkeleton();
        this.providerConfigurationProperties = providerBootStrap.getProviderConfigurationProperties();
    }

//    private int tpsLimit = 20;

    final Map<String, SlidingTimeWindow> windows = new HashMap<>();

    public RpcResponse<Object> invoke(RpcRequest request)
    {

        RpcResponse<Object> rpcResponse = new  RpcResponse<>();
        int tpsLimit = Integer.parseInt(this.providerConfigurationProperties.getMetas().getOrDefault("tc", "20"));
        String service = request.getService();
        // trafic control
        synchronized (windows){
            SlidingTimeWindow window = windows.computeIfAbsent(service,  k->new SlidingTimeWindow());

            if( window.calcSum() > tpsLimit){
                log.debug("service: {} exceed tpslimit {}", service,tpsLimit);
                throw new RpcException("service" + service + "invoked in " +window.getSize() +"s " +
                        "[" + window.getSum() +"] larger than tpsLimit " + tpsLimit );
            }

            window.record(System.currentTimeMillis());
            log.debug("service {} in window with {}", service, window.getSum());
        }


        // 在skeleton中找到 对应的bean
        List<ProviderMeta> providerMetas = skeleton.get(request.getService());

        //
        if(request.getParameters()!=null)
        {
            for(Map.Entry <String,String> params :request.getParameters().entrySet())
            {
                RpcContext.setContextParameter(params.getKey(), params.getValue());
            }
        }

        try {

            ProviderMeta meta = findProviderMeta(providerMetas, request.getMethodSign());
            Object[] args = processArgs(request.getArgs(), meta.getMethod().getParameterTypes(), meta.getMethod().getGenericReturnType());
            Object result = meta.getMethod().invoke(meta.getServiceImpl(),args);
            rpcResponse.setData(result);
            rpcResponse.setStatus(true);

        } catch (InvocationTargetException e) {
            e.printStackTrace();        // todo delete
            rpcResponse.setException(new RpcException(e.getTargetException().getMessage()));
        } catch (IllegalAccessException e) {
            e.printStackTrace();        // todo delete
            rpcResponse.setException(new RpcException(e.getMessage()));
        }
        finally {
            RpcContext.ContextParameters.get().clear();
        }

        return rpcResponse;

    }

    /**
     * 将参数数据转换为对应的类型
     * @param args
     * @param parameterTypes
     * @return
     */
    private Object[] processArgs(Object[] args, Class<?>[] parameterTypes, Type genericReturnType) {

        if(args == null || args.length == 0) return args;
        Object[] actual = new Object[args.length];
        for(int i = 0; i<actual.length; i++){
            actual[i] = TypeUtils.castGenericType(args[i], parameterTypes[i], genericReturnType);
        }
        return actual;
    }

    private ProviderMeta findProviderMeta(List<ProviderMeta> providerMetas, String methodSign) {
        Optional<ProviderMeta> metaOptional =  providerMetas.stream().filter(x->x.getMethodSign().equals(methodSign)).findFirst();
        return metaOptional.orElse(null);
    }

}
