package io.unbong.ubrpc.core.filter;

import io.unbong.ubrpc.core.api.Filter;
import io.unbong.ubrpc.core.api.RpcContext;
import io.unbong.ubrpc.core.api.RpcRequest;
import io.unbong.ubrpc.core.api.RpcResponse;

import java.util.Map;

/**
 * Description
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-04-13 12:02
 */
public class ParameterFilter implements Filter {
    @Override
    public Object preFilter(RpcRequest request) {

        Map<String,String> map = RpcContext.ContextParameters.get();
        if(map.isEmpty()) return null;
        request.getParameters().putAll(map);
        return null;
    }

    @Override
    public Object postFilter(RpcRequest request, RpcResponse response, Object result) {

        RpcContext.ContextParameters.get().clear();
        return null;
    }

    @Override
    public Filter next() {
        return null;
    }
}
