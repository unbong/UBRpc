package io.unbong.ubrpc.core.meta;

import lombok.Data;

import java.lang.reflect.Method;

/**
 * Description
 *   方法元数据，目的是将签名与对应反射方法对应起来
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-03-13 21:18
 */

@Data
public class ProviderMeta {
    // 具体的某个方法
    Method method;          // 方法
    String methodSign;      // 方法签名
    Object serviceImpl ;    //
}
