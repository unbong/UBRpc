package io.unbong.ubrpc.core.api;

import io.unbong.ubrpc.core.meta.InstanceMeta;
import lombok.Data;

import java.util.List;

/**
 * Description
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-03-17 17:53
 */
@Data
public class RpcContext {
    List<Filter> filters;
    LoadBalancer<InstanceMeta> loadBalancer;
    Router<InstanceMeta> router;
}
