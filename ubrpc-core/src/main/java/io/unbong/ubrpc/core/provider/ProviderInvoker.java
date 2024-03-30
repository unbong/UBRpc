package io.unbong.ubrpc.core.provider;

import io.unbong.ubrpc.core.api.RpcException;
import io.unbong.ubrpc.core.api.RpcRequest;
import io.unbong.ubrpc.core.api.RpcResponse;
import io.unbong.ubrpc.core.meta.ProviderMeta;
import io.unbong.ubrpc.core.util.TypeUtils;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 *  服务端的对于访问方法的功能
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-03-21 22:01
 */
public class ProviderInvoker {

    private MultiValueMap<String, ProviderMeta> skeleton;

    public ProviderInvoker(ProviderBootStrap providerBootStrap) {
        this.skeleton = providerBootStrap.getSkeleton();
    }

    public RpcResponse<Object> invoke(RpcRequest request)
    {

        RpcResponse<Object> rpcResponse = new  RpcResponse<>();
        // 在skeleton中找到 对应的bean
//        Object bean = skeleton.get(request.getService());
        List<ProviderMeta> providerMetas = skeleton.get(request.getService());
//        for(Map.Entry <String,String> params :request.getParameters().entrySet())
//        {
//            System.out.println(params.toString());
//        }
        try {

            ProviderMeta meta = findProviderMeta(providerMetas, request.getMethodSign());
            Object[] args = processArgs(request.getArgs(), meta.getMethod().getParameterTypes());
            Object result = meta.getMethod().invoke(meta.getServiceImpl(),args);
            rpcResponse.setData(result);
            rpcResponse.setStatus(true);

        } catch (InvocationTargetException e) {
            rpcResponse.setException(new RpcException(e.getTargetException().getMessage()));
        } catch (IllegalAccessException e) {
            rpcResponse.setException(new RpcException(e.getMessage()));
        }

        return rpcResponse;

    }

    /**
     * 将参数数据转换为对应的类型
     * @param args
     * @param parameterTypes
     * @return
     */
    private Object[] processArgs(Object[] args, Class<?>[] parameterTypes) {

        if(args == null || args.length == 0) return args;
        Object[] actual = new Object[args.length];
        for(int i = 0; i<actual.length; i++){
            actual[i] = TypeUtils.cast(args[i], parameterTypes[i]);
        }
        return actual;
    }

    private ProviderMeta findProviderMeta(List<ProviderMeta> providerMetas, String methodSign) {
        Optional<ProviderMeta> metaOptional =  providerMetas.stream().filter(x->x.getMethodSign().equals(methodSign)).findFirst();
        return metaOptional.orElse(null);
    }

}
