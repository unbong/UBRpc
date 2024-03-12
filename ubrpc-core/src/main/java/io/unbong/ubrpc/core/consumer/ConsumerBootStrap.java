package io.unbong.ubrpc.core.consumer;

import io.unbong.ubrpc.core.annotation.UBConsumer;
import lombok.Data;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-03-10 20:47
 */
@Data
public class ConsumerBootStrap implements ApplicationContextAware {

    ApplicationContext _applicatoinContext ;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        _applicatoinContext = applicationContext;
    }

    private Map<String, Object> stub = new HashMap<>();


    /**
     * 执行自定义的依赖注入
     */
    public void start(){
        String[] names = _applicatoinContext.getBeanDefinitionNames();
        for(String name : names){
            Object bean = _applicatoinContext.getBean(name);
            List<Field> fields = findAnnotatedField(bean.getClass());

            if(!name.contains("ubrpcDemoConsumerApplication")) continue;
            //　创建代理对象，并设定

            fields.stream().forEach(f->{
                try {
                    Class<?> service = f.getType();
                    String serviceName = service.getCanonicalName();
                    Object consumer = stub.get(serviceName);
                    if(consumer == null){
                        consumer = createConsumerProxy(service);

                    }
                    f.setAccessible(true);

                    f.set(bean, consumer);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            });
        }
    }

    /**
     * 通过 动态代理 bytebuddy
     * @param service
     * @return
     */
    private Object createConsumerProxy(Class<?> service) {
        return Proxy.newProxyInstance(service.getClassLoader(),
                new Class[]{service}
                , new UBInvocationHandler(service){}

        );
    }


    private List<Field> findAnnotatedField(Class<?> aClass) {
        List<Field> result = new ArrayList<>();
        // ub Spring 启动后的类是代理过的 不是原始定义的类
        // ub
        while (aClass != null)
        {
            Field[] fields = aClass.getDeclaredFields();
            for(Field field : fields){
                if(field.isAnnotationPresent(UBConsumer.class))
                {
                    result.add(field);
                }
            }
            aClass = aClass.getSuperclass();
        }
        return result;
    }
}
