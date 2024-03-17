package io.unbong.ubrpc.core.api;

import io.unbong.ubrpc.core.cluster.RandomLoadBalancer;
import io.unbong.ubrpc.core.cluster.RoundRibonLoadBalancer;

import java.util.List;

/**
 * Description
 *  负载均衡： 权重 AAWR（自适应），轮询
 *
 *   自适应
 *      8081 10ms
 *      8082 100ms
 *      统计完服务响应时间后，得出服务的平均能力，并更行相应的权重
 *      avg*0.3  + last*0.7  得出总权重
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-03-17 16:10
 */
public interface LoadBalancer<T> {

    /**
     * 从大集合中选择小的集合
     * @param providers
     * @return
     */
    T choose(List<T> providers);

    /**
     *  默认的实现即为Static
     */
    LoadBalancer Default = new RoundRibonLoadBalancer();
}
