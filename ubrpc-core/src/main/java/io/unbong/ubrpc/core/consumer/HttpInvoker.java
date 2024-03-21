package io.unbong.ubrpc.core.consumer;

import io.unbong.ubrpc.core.api.RpcRequest;
import io.unbong.ubrpc.core.api.RpcResponse;

/**
 * Description
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-03-21 22:46
 */
public interface HttpInvoker {

    public RpcResponse<?> post(RpcRequest rpcRequest, String url) ;
}
