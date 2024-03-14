package io.unbong.ubrpc.core.util;

import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Description
 *  类型转换工具类
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-03-13 21:52
 */
public class TypeUtils {

    public static Object cast(Object origin, Class<?> type)
    {
        if(origin == null) return null;
        Class<?> aClass = origin.getClass();
        if(type.isAssignableFrom(aClass))
        {
            return origin;
        }

        // 数组类型
        if(type.isArray())
        {
            if(origin instanceof List list)
            {
                origin = list.toArray();
            }
            int len = Array.getLength(origin);
            Class<?> componentType = type.getComponentType();
            Object resultArray = Array.newInstance(componentType, len);
            for(int i = 0; i<len; i++){
                Array.set(resultArray, i, Array.get(origin, i));
            }

            return resultArray;
        }

        // 穿过来的数据为对象类型时
        if(origin instanceof HashMap map)
        {
            JSONObject jsonObject = new JSONObject(map);
            return jsonObject.toJavaObject(type);
        }

        // 基本类型
        if(type.equals(Integer.class)|| type.equals(Integer.TYPE))
        {
            return Integer.valueOf(origin.toString());
        }
        else if(type.equals(Long.class)|| type.equals(Long.TYPE))
        {
            return Long.valueOf(origin.toString());
        }
        else if(type.equals(Short.class)|| type.equals(Short.TYPE))
        {
            return Short.valueOf(origin.toString());
        }
        else if(type.equals(Float.class)|| type.equals(Float.TYPE))
        {
            return Float.valueOf(origin.toString());
        }
        else if(type.equals(Double.class)|| type.equals(Double.TYPE))
        {
            return Double.valueOf(origin.toString());
        }
        else if(type.equals(Character.class)|| type.equals(Character.TYPE))
        {
            return Character.valueOf(origin.toString().charAt(0));
        }
        else if(type.equals(Byte.class)|| type.equals(Byte.TYPE))
        {
            return Byte.valueOf(origin.toString());
        }
        else if(type.equals(Boolean.class)|| type.equals(Boolean.TYPE))
        {
            return Boolean.valueOf(origin.toString());
        }

        return null;
    }
}
