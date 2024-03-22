package io.unbong.ubrpc.core.provider;

import io.unbong.ubrpc.core.api.RegistryCenter;
import io.unbong.ubrpc.core.registry.zk.ZKRegistryCenter;
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
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner provider_runner(@Autowired ProviderBootStrap providerBootStrap){
        return x->{
            System.out.println("providerBootStrap starting..." );
            providerBootStrap.start();
            System.out.println("providerBootStrap started ..." );
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
        return new ZKRegistryCenter();
    }


}
