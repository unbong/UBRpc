package io.unbong.ubrpc.core.consumer;

import io.unbong.ubrpc.core.annotation.UBConsumer;
import io.unbong.ubrpc.core.api.*;
import io.unbong.ubrpc.core.meta.InstanceMeta;
import io.unbong.ubrpc.core.meta.ServiceMeta;
import io.unbong.ubrpc.core.registry.ChangedListener;
import io.unbong.ubrpc.core.registry.Event;
import io.unbong.ubrpc.core.util.MethodUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
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
@Slf4j
public class ConsumerBootStrap implements ApplicationContextAware, EnvironmentAware {

    ApplicationContext applicationContext;
    Environment _environment;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private Map<String, Object> stub = new HashMap<>();


    /**
     * 执行自定义的依赖注入
     */
    public void start(){

        Router<InstanceMeta> router = applicationContext.getBean(Router.class);
        LoadBalancer<InstanceMeta> loadBalancer = applicationContext.getBean(LoadBalancer.class);
        RegistryCenter rc = applicationContext.getBean(RegistryCenter.class);
        List<Filter> filters  = applicationContext.getBeansOfType(Filter.class).
                        values().stream().toList();

        RpcContext context = applicationContext.getBean(RpcContext.class);
        context.setRouter(router);
        context.setLoadBalancer(loadBalancer);
        context.setFilters(filters);
//        context.setParameters(new HashMap<>());
//        context.getParameters().put("app.timeout",timeout);
//        context.getParameters().put("app.retries", retries);

        String[] names = applicationContext.getBeanDefinitionNames();
        for(String name : names){
            Object bean = applicationContext.getBean(name);
            List<Field> fields = MethodUtil.findAnnotatedField(bean.getClass(), UBConsumer.class);

            if(!name.contains("ubrpcDemoConsumerApplication")) continue;
            //　创建代理对象，并设定

            fields.stream().forEach(f->{
                try {
                    Class<?> service = f.getType();
                    String serviceName = service.getCanonicalName();
                    Object consumer = stub.get(serviceName);
                    if(consumer == null){
                        consumer = createConsumerFromRegistry(service, context, rc);
                    //createConsumerProxy(service, context, List.of(providers));
                        stub.put(serviceName, consumer);

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
     * 处理服务与Consumer的关系
     * @param service
     * @param rpcContext
     * @param rc
     * @return
     */
    private Object createConsumerFromRegistry(Class<?> service, RpcContext rpcContext, RegistryCenter rc) {
        String serviceName = service.getCanonicalName();

        ServiceMeta serviceMeta = ServiceMeta.builder()
                .name(serviceName)
                .app(rpcContext.getParameter("app.id"))
                .namespace(rpcContext.getParameter("app.namespace"))
                .env(rpcContext.getParameter("app.env"))
                .build();
        List<InstanceMeta> providers = rc.fetchAll(serviceMeta);

        // 订阅节点更新
        rc.subscribe(serviceMeta, new ChangedListener() {
            @Override
            public void fire(Event event) {
                providers.clear();
                providers.addAll(event.getData());
            }
        });
        return createConsumerProxy(service, rpcContext, providers);
    }



    /**
     * 创建动态代理
     *
     * @param service
     * @param providers
     * @return
     */
    private Object createConsumerProxy(Class<?> service, RpcContext context, List<InstanceMeta> providers) {
        return Proxy.newProxyInstance(service.getClassLoader(),
                new Class[]{service}
                , new UBInvocationHandler(service,  context, providers){}
        );
    }



    @Override
    public void setEnvironment(Environment environment) {
        _environment= environment;
    }
}
