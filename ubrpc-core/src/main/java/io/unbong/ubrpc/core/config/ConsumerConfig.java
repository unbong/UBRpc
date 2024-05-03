package io.unbong.ubrpc.core.config;

import io.unbong.ubrpc.core.api.*;
import io.unbong.ubrpc.core.cluster.GrayRouter;
import io.unbong.ubrpc.core.consumer.ConsumerBootStrap;
import io.unbong.ubrpc.core.filter.CacheFilter;
import io.unbong.ubrpc.core.filter.ParameterFilter;
import io.unbong.ubrpc.core.meta.InstanceMeta;
import io.unbong.ubrpc.core.registry.ub.UbRegistryCenter;
import io.unbong.ubrpc.core.registry.zk.ZKRegistryCenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;

import java.util.List;

/**
 * Description
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-03-10 20:49
 */
@Configuration
@Slf4j
@Import({AppConfigProperties.class,ConsumerConfigurationProperties.class})
public class ConsumerConfig {

//    @Value("${ubrpc.providers}")
//    String servers;
//
//    @Value("${app.grayRatio:0}")
//    private int grayRatio;
//
//
//    @Value("${app.id:app1}")
//    private String app;
//    @Value("${app.namespace:public}")
//    private String namespace;
//    @Value("${app.env:dev}")
//    private String env;
//
//    @Value("${app.retries:1}")
//    private String retries;
//
//    @Value("${app.timeout:1000}")
//    private String timeout;
//
//    @Value("${app.faultLimit:10}")
//    private int faultLimit;
//
//    @Value("${app.halfOpenInitialDelay:10000}")
//    private int halfOpenInitialDelay;
//
//    @Value("${app.halfOpenDelay:60000}")
//    private int halfOpenDelay;
    @Autowired
    AppConfigProperties appConfigProperties;

    @Autowired
    ConsumerConfigurationProperties consumerConfigProperties;

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
//    @Bean
//    @Order(Integer.MIN_VALUE-1)
//    public Filter cacheFilter(){
//        return Filter.Default;
//    }

    @Bean
    public Filter defaultFilter(){
        return  new ParameterFilter();
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
        return new GrayRouter(consumerConfigProperties.getGrayRatio());
    }

    /**
     *
     * @return
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    public RegistryCenter comsumer_rc(){

        //return new ZKRegistryCenter();
        return new UbRegistryCenter();
    }


    /**
     * 创建RpcContextBean
     *  在启动Spring时读取完配置，并写入到RpcContext中并且
     * @param router
     * @param loadBalancer
     * @param filters
     * @return
     */
    @Bean
    public RpcContext createContext(@Autowired Router router,
                                    @Autowired LoadBalancer loadBalancer,
                                    @Autowired List<Filter> filters) {
        RpcContext context = new RpcContext();
        context.setRouter(router);
        context.setLoadBalancer(loadBalancer);
        context.setFilters(filters);
        context.getParameters().put("app.id", appConfigProperties.getId());
        context.getParameters().put("app.namespace", appConfigProperties.getNamespace());
        context.getParameters().put("app.env", appConfigProperties.getEnv());
        context.getParameters().put("consumer.retries", String.valueOf(consumerConfigProperties.getRetries()));
        context.getParameters().put("consumer.timeout", String.valueOf(consumerConfigProperties.getTimeout()));
        context.getParameters().put("consumer.halfOpenInitialDelay", String.valueOf(consumerConfigProperties.getHalfOpenInitialDelay()));
        context.getParameters().put("consumer.faultLimit", String.valueOf(consumerConfigProperties.getFaultLimit()));
        context.getParameters().put("consumer.halfOpenDelay", String.valueOf(consumerConfigProperties.getHalfOpenDelay()));
        return context;
    }
}
