package io.unbong.ubrpc.core.provider;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
