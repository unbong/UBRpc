package io.unbong.ubrpc.core.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * todo
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpcResponse<T> {

    boolean status;
    T data;

    Exception exception;
}
