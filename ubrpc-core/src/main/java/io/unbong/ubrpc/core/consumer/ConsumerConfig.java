package io.unbong.ubrpc.core.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * Description
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-03-10 20:49
 */
@Configuration
public class ConsumerConfig {

    @Bean
    public ConsumerBootStrap createConsumerBootStrap(){
        return new ConsumerBootStrap();
    }

    /**
     * Spring上下文初始换完成后 会调用start
     * ApplicationRunner 出错时 会把程序挡掉
     * @param consumerBootStrap
     * @return
     */
    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner consumer_runner(@Autowired ConsumerBootStrap consumerBootStrap){
        return x->{
            System.out.println("consumerBootStrap starting..." );
            consumerBootStrap.start();
            System.out.println("consumerBootStrap started ..." );
        };
    }
}
