package io.unbong.ubrpc.demo.provider;

import io.unbong.ubconfig.client.annotation.EnableUbConfig;
import io.unbong.ubrpc.core.api.RpcRequest;
import io.unbong.ubrpc.core.api.RpcResponse;
import io.unbong.ubrpc.core.config.ProviderConfig;
import io.unbong.ubrpc.core.config.ProviderConfigurationProperties;
import io.unbong.ubrpc.core.trasport.SpringTransport;
import io.unbong.ubrpc.demo.api.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * todo
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 */

@SpringBootApplication
@RestController
@Import({ProviderConfig.class})
@EnableUbConfig
//@EnableApolloConfig
@Slf4j
public class UbrpcDemoProviderApplication {

//    @Bean
//    ApolloChangedListener apolloChangedListener() {
//        return new ApolloChangedListener();
//    }

    public static void main(String[] args) {
        SpringApplication.run(UbrpcDemoProviderApplication.class, args);
    }

    @Autowired
    SpringTransport transport;

    @Autowired
    UserService userService;

    @Autowired
    ProviderConfigurationProperties providerConfigurationProperties ;
    @RequestMapping("/ports")
    public RpcResponse<String> ports(@RequestParam("ports") String ports)
    {
        userService.setTimeoutPorts(ports);
        RpcResponse<String> response = new RpcResponse<>();
        response.setStatus(true);
        response.setData("ok: "+ ports);
        return response;
    }

    @RequestMapping("/meta")
    public String meta(){
        Map<String, String> metas =providerConfigurationProperties.getMetas();
        log.debug("metas hashcode " + metas.hashCode());
        return metas.toString();
    }

    @Bean
    ApplicationRunner providerRun() {
        return x -> {
            // test 1 parameter method
            RpcRequest request = new RpcRequest();
            request.setService("io.unbong.ubrpc.demo.api.UserService");
            request.setMethodSign("findById@1_int");
            request.setArgs(new Object[]{100});

            RpcResponse<Object> rpcResponse = transport.invoke(request);
            log.info("return : "+rpcResponse.getData());

            // test 2 parameters method
            RpcRequest request1 = new RpcRequest();
            request1.setService("io.unbong.ubrpc.demo.api.UserService");
            request1.setMethodSign("find@2_int_java.lang.String");
            request1.setArgs(new Object[]{100, "CC"});

            RpcResponse<Object> rpcResponse1 = transport.invoke(request1);
            log.info("return : "+rpcResponse1.getData());


//            for (int i = 0; i < 1000; i++) {
//                try {
//                    Thread.sleep(1000);
//                    RpcResponse<Object> r = transport.invoke(request);
//                    System.out.println(i + " ***>>> " +r.getData());
//                } catch (RpcException e) {
//                    // ignore
//                    System.out.println(i + " ***>>> " +e.getMessage() + " -> " + e.getErrorCode());
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            }
        };
    }
//    @Autowired
//    ProviderInvoker providerInvoker;
//    @RequestMapping("/")
//    public RpcResponse<Object> invoke(@RequestBody RpcRequest request){
//        return providerInvoker.invoke(request);
//    }

}
