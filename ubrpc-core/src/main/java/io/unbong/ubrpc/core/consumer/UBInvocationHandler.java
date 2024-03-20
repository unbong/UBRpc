package io.unbong.ubrpc.core.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.unbong.ubrpc.core.api.*;
import io.unbong.ubrpc.core.util.MethodUtil;
import io.unbong.ubrpc.core.util.TypeUtils;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Description
 *
 * 动态代理的代理类
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-03-10 21:02
 */
public class UBInvocationHandler implements InvocationHandler {


    final static MediaType JSONTYPE = MediaType.get("application/json; charset=utf-8");
    Class<?> service ;
    RpcContext _context;
    List<String> _providers;

    public UBInvocationHandler(Class<?> clazz, RpcContext context, List<String> providers){
        this.service = clazz;
        _context = context;
        _providers = providers;
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
        rpcRequest.setMethodSign(MethodUtil.method(method));
        rpcRequest.setArgs(args);


        List<String> urls= _context.getRouter().route(_providers);
        String url = (String)_context.getLoadBalancer().choose(urls);
        System.out.println("loadBlancer.choose(urls)==>  " + url);
        RpcResponse rpcResponse = post(rpcRequest,url);

        if(rpcResponse.isStatus()){

            Object rpcData =  rpcResponse.getData();

            if(rpcData instanceof JSONObject)
            {
                JSONObject data = (JSONObject)rpcData;
                return data.toJavaObject(method.getReturnType());
            }else if(rpcData instanceof JSONArray jsonArray)
            {
                // 数组类型的反序列化操作
                Object[] array = jsonArray.toArray();
                // 返回数组的类型 仅仅是类型
                Class<?> componentType = method.getReturnType().getComponentType();

                Object resultArray  = Array.newInstance(componentType, array.length);
                for (int i = 0; i < array.length; i++) {
                    Array.set(resultArray, i, array[i]);
                }
                return resultArray;
            }

            // ub JSONObject to
            return  TypeUtils.cast(rpcData, method.getReturnType());
        }
        else{
            Exception ex = rpcResponse.getException();
            //ex.printStackTrace();

            throw new RuntimeException(ex);

        }
//        return null;
    }

    OkHttpClient client = new OkHttpClient.Builder()
            .connectionPool(new ConnectionPool(16, 60, TimeUnit.SECONDS))
            .readTimeout(1000,TimeUnit.SECONDS)
            .connectTimeout(1000,TimeUnit.SECONDS )
            .build();
    private RpcResponse post(RpcRequest rpcRequest,String url) {

        String reqJson = JSON.toJSONString(rpcRequest);
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(reqJson,  JSONTYPE))
                .build();

        try {
            String resJson = client.newCall(request)
                    .execute().body().string();
            System.out.println(" ===> respJson = " + resJson);
            RpcResponse rpcResponse = JSON.parseObject(resJson,RpcResponse.class);
            return rpcResponse;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }
}
