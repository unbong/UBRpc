package io.unbong.ubrpc.demo.provider;

import io.unbong.ubrpc.core.api.RpcRequest;
import io.unbong.ubrpc.core.api.RpcResponse;
import io.unbong.ubrpc.core.provider.ProviderBootStrap;
import io.unbong.ubrpc.core.provider.ProviderConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * todo
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 */

@SpringBootApplication
@RestController
@Import({ProviderConfig.class})
public class UbrpcDemoProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(UbrpcDemoProviderApplication.class, args);
    }

    @Autowired
    ProviderBootStrap providerBootStrap ;
    @RequestMapping("/")
    public RpcResponse invoke(@RequestBody RpcRequest request){
        return providerBootStrap.invoke(request);
    }

}
