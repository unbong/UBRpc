package io.unbong.ubrpc.core.api;

import lombok.Data;
import lombok.ToString;

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
}
