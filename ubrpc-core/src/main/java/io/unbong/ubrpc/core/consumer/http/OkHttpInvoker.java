package io.unbong.ubrpc.core.consumer.http;

import com.alibaba.fastjson.JSON;
import io.unbong.ubrpc.core.api.RpcRequest;
import io.unbong.ubrpc.core.api.RpcResponse;
import io.unbong.ubrpc.core.consumer.HttpInvoker;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Description
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-03-21 22:47
 */
@Slf4j
public class OkHttpInvoker implements HttpInvoker {
    final static MediaType JSONTYPE = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client;
    public OkHttpInvoker() {
       client  = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(16, 60, TimeUnit.SECONDS))
                .readTimeout(100,TimeUnit.SECONDS)
                .connectTimeout(1000,TimeUnit.SECONDS )
                .build();
    }

    @Override
    public RpcResponse<?> post(RpcRequest rpcRequest,String url) {

        String reqJson = JSON.toJSONString(rpcRequest);
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(reqJson,  JSONTYPE))
                .build();

        try {
            String resJson = client.newCall(request)
                    .execute().body().string();
            log.debug(" ===> respJson = " + resJson);
            RpcResponse<Object> rpcResponse = JSON.parseObject(resJson,RpcResponse.class);
            return rpcResponse;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }
}
