package io.unbong.ubrpc.core.trasport;

import io.unbong.ubrpc.core.api.RpcRequest;
import io.unbong.ubrpc.core.api.RpcResponse;
import io.unbong.ubrpc.core.provider.ProviderInvoker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-04-13 14:32
 */

@RestController
public class SpringTransport {


    @Autowired
    ProviderInvoker providerInvoker;

    @RequestMapping("/ubrpc")
    public RpcResponse invoke(@RequestBody RpcRequest request){
        return providerInvoker.invoke(request);
    }
}
