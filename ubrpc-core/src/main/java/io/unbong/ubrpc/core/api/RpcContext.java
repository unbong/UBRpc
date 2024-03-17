package io.unbong.ubrpc.core.api;

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
    LoadBalancer loadBalancer;
    Router router;
}
