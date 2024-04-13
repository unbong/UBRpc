package io.unbong.ubrpc.core.api;

import lombok.Data;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * todo
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 */
@Data
@ToString
public class RpcRequest {

    String service;    //io.unbong.ubrpc.demo.api.UserService
    String methodSign;
    Object[] args;
    Map<String, String> parameters = new HashMap<>();
}
