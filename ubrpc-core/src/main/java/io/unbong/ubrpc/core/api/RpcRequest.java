package io.unbong.ubrpc.core.api;

import lombok.Data;

/**
 * todo
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 */
@Data
public class RpcRequest {

    String service;    //io.unbong.ubrpc.demo.api.UserService
    String methodSign;

    Object[] args;
}
