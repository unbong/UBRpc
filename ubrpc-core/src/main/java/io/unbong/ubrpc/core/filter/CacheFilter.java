package io.unbong.ubrpc.core.filter;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.unbong.ubrpc.core.api.Filter;
import io.unbong.ubrpc.core.api.RpcRequest;
import io.unbong.ubrpc.core.api.RpcResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 如果调用过，直接返回缓存
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-03-23 21:29
 */
public class CacheFilter implements Filter {

    Cache<String, Object> cache = CacheBuilder.newBuilder()
            .maximumSize(40)
            .expireAfterAccess(3, TimeUnit.MINUTES)
            .build();

    @Override
    public Object preFilter(RpcRequest request) {

        Object o = cache.getIfPresent(request.toString());
        return o;

    }

    @Override
    public Object postFilter(RpcRequest request, RpcResponse response, Object result) {
        cache.put(request.toString(), result );

        return result;
    }

    @Override
    public Filter next() {
        return null;
    }
}
