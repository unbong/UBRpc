package io.unbong.ubrpc.core.provider;

import io.unbong.ubrpc.core.annotation.UbProvider;
import io.unbong.ubrpc.core.api.RegistryCenter;
import io.unbong.ubrpc.core.meta.InstanceMeta;
import io.unbong.ubrpc.core.meta.ProviderMeta;
import io.unbong.ubrpc.core.meta.ServiceMeta;
import io.unbong.ubrpc.core.util.MethodUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.Map;

/**
 *
 * 3
 *  1 将签名与方法保存起来
 *
 *   注册中心
 *
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 */
@Data
@Slf4j
public class ProviderBootStrap implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    private String port;
    private String app;
    private String namespace;
    private String env;
    private Map<String, String> metas;

    private MultiValueMap<String, ProviderMeta> skeleton = new LinkedMultiValueMap<>();
    private InstanceMeta _instance;

    RegistryCenter rc;

    public ProviderBootStrap(String port, String app, String namespace,
                             String env, Map<String, String> metas) {
        this.port = port;
        this.app = app;
        this.namespace = namespace;
        this.env = env;
        this.metas = metas;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    // 为每个服务创建多值的方法元数据

    /**
     * s
     */
    @SneakyThrows
    public void start(){
        // ip and port
        String ip = InetAddress.getLocalHost().getHostAddress();
        this._instance =InstanceMeta.http(ip, Integer.valueOf(port));
        this._instance.getParameters().putAll(this.metas);
        rc.start();
        // 注册服务列表
        skeleton.keySet().forEach(this::registerService);


    }

    @PreDestroy
    public void stop(){
        skeleton.keySet().forEach(this::unregisterService);
        rc.stop();
    }

    private void unregisterService(String service) {
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .name(service)
                .app(this.app)
                .namespace(this.namespace)
                .env(this.env)
                .build();

        rc.unregister(serviceMeta, _instance);
    }

    /**
     * 在spring启动完毕后会进行注册
     *   1
     */
    @PostConstruct
    public void init(){
        Map<String, Object> providers = applicationContext.getBeansWithAnnotation(UbProvider.class);
        rc = applicationContext.getBean(RegistryCenter.class);
        // 基于skeleton创建服务列表
        providers.keySet().forEach(log::info);
        providers.values().forEach(v->{
            getInterface(v);
        });
    }

    /**
     * 注册到zookeeper
     * @param service
     */
    private void registerService(String service) {
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .name(service)
                .app(this.app)
                .namespace(this.namespace)
                .env(this.env)
                .build();

        rc.register(serviceMeta, _instance);
    }


    private void getInterface(Object impl) {

        // 实现多个接口时 将所有接口的方法元数据收集
        Class<?>[] itfers = impl.getClass().getInterfaces();
        for (Class<?> service : itfers){
            Method[] methods = service.getMethods();
            // 本地方法
            for(Method m : methods){
                if (MethodUtil.checkLocalMethod(m)){
                    continue;
                }
                createProvider(service, impl, m);
            }
        }
    }



    /**
     * 创建方法元数据
     * @param service
     * @param impl
     * @param m
     */
    private void createProvider(Class<?> service, Object impl, Method m) {
        ProviderMeta meta = ProviderMeta.builder()
                .serviceImpl(impl)
                .method(m)
                .methodSign(MethodUtil.methodSign(m))
                .build();
        log.info("create a provider: "+meta );
        skeleton.add(service.getCanonicalName(), meta);

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
