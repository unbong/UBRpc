package io.unbong.ubrpc.core.cluster;

import io.unbong.ubrpc.core.api.LoadBalancer;

import java.util.List;
import java.util.Random;

/**
 * Description
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-03-17 17:19
 */
public class RandomLoadBalancer<T> implements LoadBalancer<T> {
    Random random = new Random();
    @Override
    public T choose(List<T> providers) {
        if(providers== null|| providers.size()==0) return null;
        if(providers.size() == 1) return providers.get(0);

        // 随机返回 provider 大小内的服务名
        return providers.get(random.nextInt(providers.size()));
    }
}
