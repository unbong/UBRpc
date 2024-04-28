package io.unbong.ubrpc.core.provider;

import io.unbong.ubrpc.core.api.RegistryCenter;
import io.unbong.ubrpc.core.registry.ub.UbRegistryCenter;
import io.unbong.ubrpc.core.registry.zk.ZKRegistryCenter;
import io.unbong.ubrpc.core.trasport.SpringTransport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;

import java.util.Map;

/**
 * todo
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 */

@Configuration
@Slf4j
@Import({SpringTransport.class})
public class ProviderConfig {


    @Value("${server.port}")
    private String port;
    @Value("${app.id}")
    private String app;
    @Value("${app.namespace}")
    private String namespace;
    @Value("${app.env}")
    private String env;
    @Value("#{${app.metas}}")       //spel
    Map<String, String> metas;

    @Bean
    public ProviderBootStrap boot()
    {
        return new ProviderBootStrap(port, app, namespace, env, metas);
    }

    /**
     * 与spring生命周期中的refresh事件时期差不多
     * @param providerBootStrap
     * @return
     */
    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner provider_runner(@Autowired ProviderBootStrap providerBootStrap){
        return x->{
            log.info("providerBootStrap starting..." );
            providerBootStrap.start();
            log.info("providerBootStrap started ..." );
        };
    }

    @Bean
    public ProviderInvoker providerInvoker(@Autowired ProviderBootStrap providerBootStrap){
        return new ProviderInvoker(providerBootStrap);
    }

    /**
     *
     * @return
     */
    @Bean //(initMethod = "start", destroyMethod = "stop")
    public RegistryCenter provider_rc()
    {
        //return new ZKRegistryCenter();
        return new UbRegistryCenter();
    }


}
