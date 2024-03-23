package io.unbong.ubrpc.core.filter;

import io.unbong.ubrpc.core.api.Filter;
import io.unbong.ubrpc.core.api.RpcRequest;
import io.unbong.ubrpc.core.api.RpcResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 如果调用过，直接返回缓存
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-03-23 21:29
 */
public class CacheFilter implements Filter {

    static Map<String,Object> cache = new ConcurrentHashMap<>();

    @Override
    public Object preFilter(RpcRequest request) {

        return cache.get(request.toString());
    }

    @Override
    public Object postFilter(RpcRequest request, RpcResponse response, Object result) {
        cache.putIfAbsent(request.toString(), result );

        return result;
    }

    @Override
    public Filter next() {
        return null;
    }
}
