package io.unbong.ubrpc.core.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import io.unbong.ubrpc.core.api.RpcRequest;
import io.unbong.ubrpc.core.api.RpcResponse;
import io.unbong.ubrpc.core.consumer.http.OkHttpInvoker;
import io.unbong.ubrpc.core.meta.InstanceMeta;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-03-21 22:46
 */
public interface HttpInvoker {
    Logger log = LoggerFactory.getLogger(HttpInvoker.class);

    HttpInvoker Default = new OkHttpInvoker(500);

    public String post(String requestString, String url);
    RpcResponse post(RpcRequest rpcRequest, String url);
    String get(String url);

    @SneakyThrows
    static <T> T httpGet(String url, Class<T> clazz) {
        log.debug(" =====>>>>>> httpGet: " + url);
        String respJson = Default.get(url);
        log.debug(" =====>>>>>> response: " + respJson);
        return JSON.parseObject(respJson, clazz);
    }

    @SneakyThrows
    static <T> T httpGet(String url, TypeReference<T> typeReference) {
        log.debug(" =====>>>>>> httpGet: " + url);
        String respJson = Default.get(url);
        log.debug(" =====>>>>>> response: " + respJson);
        return JSON.parseObject(respJson, typeReference);
    }

    @SneakyThrows
    static <T> T httpPost(String requestString,String url, Class<T> clazz) {
        log.debug(" =====>>>>>> httpGet: " + url);
        String respJson = Default.post(requestString, url);
        log.debug(" =====>>>>>> response: " + respJson);
        return JSON.parseObject(respJson, clazz);
    }
}
