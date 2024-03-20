package io.unbong.ubrpc.core.provider;

import io.unbong.ubrpc.core.api.RegistryCenter;
import io.unbong.ubrpc.core.consumer.ConsumerBootStrap;
import io.unbong.ubrpc.core.registry.ZKRegistryCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * todo
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 */

@Configuration
public class ProviderConfig {

    @Bean
    public ProviderBootStrap boot()
    {
        return new ProviderBootStrap();
    }

    /**
     * 与spring生命周期中的refresh事件时期差不多
     * @param providerBootStrap
     * @return
     */
    @Bean
    public ApplicationRunner consumer_runner(@Autowired ProviderBootStrap providerBootStrap){
        return x->{
            System.out.println("providerBootStrap starting..." );
            providerBootStrap.init();
            System.out.println("providerBootStrap started ..." );
        };
    }

    /**
     *
     * @return
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    public RegistryCenter provider_rc()
    {
        return new ZKRegistryCenter();
    }


}
