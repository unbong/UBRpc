package io.unbong.ubrpc.core.provider;

import io.unbong.ubrpc.core.annotation.UbProvider;
import io.unbong.ubrpc.core.api.RpcRequest;
import io.unbong.ubrpc.core.api.RpcResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * todo
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 */
public class ProviderBootStrap implements ApplicationContextAware {

    ApplicationContext _applicationContext ;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        _applicationContext = applicationContext;
    }
    private Map<String, Object> skeleton = new HashMap<>();

    @PostConstruct
    public void buildProvider(){
        Map<String, Object> map =_applicationContext.getBeansWithAnnotation(UbProvider.class);
        map.values().forEach(v->{
            getInterface(v);
        });
    }

    private void getInterface(Object v) {
        skeleton.put(v.getClass().getInterfaces()[0].getCanonicalName(), v);
    }


    public RpcResponse invoke(RpcRequest request)
    {
        // 为了应对重载，还需要根据方法签名来进行过滤
        for(Method objMethod : Object.class.getMethods())
        {
            if(request.getMethod().equals(objMethod.getName())  )
            {
                return null;
            }
        }

        RpcResponse rpcResponse = new  RpcResponse();
        // 在skeleton中找到 对应的bean
        Object bean = skeleton.get(request.getService());
        Method method = findMethod(bean.getClass(),request.getMethod());

        try {
            Object result = method.invoke(bean,request.getArgs() );
            rpcResponse.setData(result);
            rpcResponse.setStatus(true);

        } catch (InvocationTargetException e) {
            rpcResponse.setException(new RuntimeException(e.getTargetException().getMessage()));
        } catch (IllegalAccessException e) {
            rpcResponse.setException(new RuntimeException(e.getMessage()));
        }

        return rpcResponse;

    }

    private Method findMethod(Class<?> aClass, String methodName) {

        for(Method method: aClass.getMethods())
        {
            if(method.getName().equals(methodName)){
                return  method;
            }
        }
        return null;
    }

}
