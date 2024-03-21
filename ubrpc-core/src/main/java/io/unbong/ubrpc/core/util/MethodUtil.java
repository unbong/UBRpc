package io.unbong.ubrpc.core.util;

import io.unbong.ubrpc.core.annotation.UBConsumer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Description
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-03-13 21:10
 */
public class MethodUtil {

    /**
     * 生成方法签名 方法名@参数个数_参数类型 。。。
     * @param method
     * @return
     */
    public static String method(Method method)
    {
        StringBuilder sb = new StringBuilder(method.getName());
        sb.append("@").append(method.getParameterCount());
        Arrays.stream(method.getParameterTypes()).forEach(
                c->sb.append("_").append(c.getCanonicalName())
        );
        return sb.toString();
    }

    public static boolean checkLocalMethod(String method) {

        if ("toString".equals(method) ||
                "hashCode".equals(method) ||
                "notifyAll".equals(method) ||
                "equals".equals(method) ||
                "wait".equals(method) ||
                "getClass".equals(method) ||
                "notify".equals(method)) {
            return true;
        }
        return false;
    }

    public static boolean checkLocalMethod(Method m) {

        return m.getDeclaringClass().equals(Object.class);

    }

    /**
     * 获得注解标注的所有属性
     * @param aClass
     * @param annotationClass
     * @return
     */
    public static List<Field> findAnnotatedField(Class<?> aClass, Class<? extends Annotation> annotationClass ) {
        List<Field> result = new ArrayList<>();
        // ub Spring 启动后的类是代理过的 不是原始定义的类
        // ub
        while (aClass != null)
        {
            Field[] fields = aClass.getDeclaredFields();
            for(Field field : fields){
                if(field.isAnnotationPresent(annotationClass))
                {
                    result.add(field);
                }
            }
            aClass = aClass.getSuperclass();
        }
        return result;
    }


}
