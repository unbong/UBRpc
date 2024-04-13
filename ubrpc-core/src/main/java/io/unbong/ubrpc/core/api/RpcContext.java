package io.unbong.ubrpc.core.api;

import io.unbong.ubrpc.core.meta.InstanceMeta;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    Map<String, String> parameters;

    public static ThreadLocal<Map<String, String>> ContextParameters = new ThreadLocal<>(){
        @Override
        protected Map<String, String> initialValue() {
            return new HashMap<>();
        }
    };

    public static void setContextParameter(String key,String value){
        ContextParameters.get().put(key,value);
    }

    public static String getContextParameter(String key){
        return ContextParameters.get().get(key);
    }

    public static void removeContextParameter(String key){
        ContextParameters.get().remove(key);
    }
}
