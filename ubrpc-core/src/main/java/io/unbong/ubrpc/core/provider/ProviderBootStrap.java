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
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
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
public class ProviderBootStrap implements ApplicationContextAware {

    ApplicationContext _applicationContext ;
    private MultiValueMap<String, ProviderMeta> skeleton = new LinkedMultiValueMap<>();
    private InstanceMeta _instance;

    RegistryCenter rc;


    @Value("${server.port}")
    private String port;


    @Value("${app.id}")
    private String app;
    @Value("${app.namespace}")
    private String namespace;
    @Value("${app.env}")
    private String env;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        _applicationContext = applicationContext;
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
        Map<String, Object> providers = _applicationContext.getBeansWithAnnotation(UbProvider.class);
        rc = _applicationContext.getBean(RegistryCenter.class);
        // 基于skeleton创建服务列表
        providers.keySet().forEach(System.out::println);
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


    private void getInterface(Object v) {

        // 实现多个接口时 将所有接口的方法元数据收集
        Class<?>[] itfers = v.getClass().getInterfaces();
        for (Class<?> itfer: itfers){
            Method[] methods = itfer.getMethods();
            // 本地方法
            for(Method m : methods){
                if (MethodUtil.checkLocalMethod(m)){
                    continue;
                }
                createProvider(itfer, v, m);
            }
        }
    }



    /**
     * 创建方法元数据
     * @param itfer
     * @param v
     * @param m
     */
    private void createProvider(Class<?> itfer, Object v, Method m) {
        ProviderMeta meta = new ProviderMeta();
        meta.setServiceImpl(v);
        meta.setMethodSign(MethodUtil.method(m));
        meta.setMethod(m);
        System.out.println("create a provider: "+meta );
        skeleton.add(itfer.getCanonicalName(), meta);

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
