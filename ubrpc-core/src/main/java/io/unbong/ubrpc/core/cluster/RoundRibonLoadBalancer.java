package io.unbong.ubrpc.core.cluster;

import io.unbong.ubrpc.core.api.LoadBalancer;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description
 *  轮询负载均衡
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-03-17 17:19
 */
public class RoundRibonLoadBalancer<T> implements LoadBalancer<T> {

    AtomicInteger index = new AtomicInteger(0);
    @Override
    public T choose(List<T> providers) {
        if(providers== null|| providers.size()==0) return null;
        if(providers.size() == 1) return providers.get(0);

        // 随机返回 provider 大小内的服务名 (符号位为零的其他为1的数进行与运算）
        return providers.get( (index.getAndIncrement()& 0x7fffffff)% providers.size());
    }
}
