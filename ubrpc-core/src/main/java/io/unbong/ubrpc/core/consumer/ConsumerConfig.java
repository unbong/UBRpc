package io.unbong.ubrpc.core.consumer;

import io.unbong.ubrpc.core.api.Filter;
import io.unbong.ubrpc.core.api.LoadBalancer;
import io.unbong.ubrpc.core.api.RegistryCenter;
import io.unbong.ubrpc.core.api.Router;
import io.unbong.ubrpc.core.cluster.GrayRouter;
import io.unbong.ubrpc.core.filter.CacheFilter;
import io.unbong.ubrpc.core.meta.InstanceMeta;
import io.unbong.ubrpc.core.registry.zk.ZKRegistryCenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
@Slf4j
public class ConsumerConfig {

    @Value("${ubrpc.providers}")
    String servers;

    @Value("${app.grayRatio}")
    private int grayRatio;
    @Bean
    public ConsumerBootStrap createConsumerBootStrap(){
        return new ConsumerBootStrap();
    }

    /**
     * Spring上下文初始化完成后 会调用start
     * ApplicationRunner 出错时 会把程序挡掉
     * @param consumerBootStrap
     * @return
     */
    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner consumer_runner(@Autowired ConsumerBootStrap consumerBootStrap){
        return x->{
            log.info("consumerBootStrap starting..." );

            consumerBootStrap.start();
            log.info("consumerBootStrap started ..." );
        };
    }

    /**
     * 缓存过滤器
     * @return
     */
    @Bean
    @Order(Integer.MIN_VALUE-1)
    public Filter cacheFilter(){
        return new CacheFilter();
    }

//    @Bean
//    @Order(Integer.MIN_VALUE)
//    public Filter mockFilter(){
//        return new MockFilter();
//    }

    /**
     * 装配默认负载均衡
     * @return
     */
    @Bean
    public LoadBalancer<InstanceMeta> loadBalancer(){
        return LoadBalancer.Default;
    }

    /**
     * 默认路由
     * @return
     */
    @Bean
    public Router<InstanceMeta> router(){
        return new GrayRouter(grayRatio);
    }

    /**
     *
     * @return
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    public RegistryCenter comsumer_rc(){
        return new ZKRegistryCenter();
    }


}
