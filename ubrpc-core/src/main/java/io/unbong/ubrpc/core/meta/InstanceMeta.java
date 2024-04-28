package io.unbong.ubrpc.core.meta;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Description
 *  描述服务实例元数据
 *
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-03-22 20:51
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InstanceMeta {

    public InstanceMeta(String schema, String host, Integer port, String context ) {
        this.schema = schema;
        this.host = host;
        this.port = port;
        this.context = context;
    }

    private String schema;  // protocol default http
    private String host;
    private Integer port;
    private String context;

    private boolean status;     // online offline
    private Map<String,String> parameters = new HashMap<>(); // which server room ...

    public String toPath() {
        return String.format("%s_%d", host, port);
    }

    public static InstanceMeta http(String host, Integer port){
        return new InstanceMeta("http", host, port, "ubrpc");
    }

    public String toURL() {
        return String.format("%s://%s:%d/%s", schema, host, port,context);
    }

    public String toMetas() {
        return JSON.toJSONString(this.getParameters());
    }

    public InstanceMeta addMeta(Map<String,String> params ) {
        this.parameters.putAll(params);
        return  this;
    }
}
