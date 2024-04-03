package io.unbong.ubrpc.core.meta;

import com.alibaba.fastjson.JSON;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Description
 *  描述服务元数据
 *
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-03-22 21:19
 */
@Data
@Builder
public class ServiceMeta {
    // todo add version

    private String app;      //
    private String namespace; // for isolation
    private String env;
    private String name;

    private Map<String,String> parameters = new HashMap<>();

    public String toPath(){
        return String.format("%s_%s_%s_%s", app, namespace, env,name);
    }

    public String toMetas() {
        return JSON.toJSONString(this.getParameters());
    }

}
