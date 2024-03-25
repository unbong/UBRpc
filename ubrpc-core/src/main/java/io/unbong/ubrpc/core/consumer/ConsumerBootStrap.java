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
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Description
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-03-10 20:47
 */
@Data
@Slf4j
public class ConsumerBootStrap implements ApplicationContextAware, EnvironmentAware {

    ApplicationContext _applicatoinContext ;
    Environment _environment;


    @Value("${app.id}")
    private String app;
    @Value("${app.namespace}")
    private String namespace;
    @Value("${app.env}")
    private String env;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        _applicatoinContext = applicationContext;
    }

    private Map<String, Object> stub = new HashMap<>();


    /**
     * 执行自定义的依赖注入
     */
    public void start(){

        Router<InstanceMeta> router = _applicatoinContext.getBean(Router.class);
        LoadBalancer<InstanceMeta> loadBalancer = _applicatoinContext.getBean(LoadBalancer.class);
        RegistryCenter rc = _applicatoinContext.getBean(RegistryCenter.class);
        List<Filter> filters  = _applicatoinContext.getBeansOfType(Filter.class).
                        values().stream().toList();

        RpcContext context = new RpcContext();
        context.setRouter(router);
        context.setLoadBalancer(loadBalancer);
        context.setFilters(filters);


        String[] names = _applicatoinContext.getBeanDefinitionNames();
        for(String name : names){
            Object bean = _applicatoinContext.getBean(name);
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
     * @param context
     * @param rc
     * @return
     */
    private Object createConsumerFromRegistry(Class<?> service, RpcContext context, RegistryCenter rc) {
        String serviceName = service.getCanonicalName();

        ServiceMeta serviceMeta = ServiceMeta.builder()
                .name(serviceName)
                .app(this.app)
                .namespace(this.namespace)
                .env(this.env)
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
        return createConsumerProxy(service, context, providers);
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
