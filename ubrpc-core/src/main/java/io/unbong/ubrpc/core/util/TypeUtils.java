package io.unbong.ubrpc.core.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.json.JSONParser;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Description
 *  类型转换工具类
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-03-13 21:52
 */
@Slf4j
public class TypeUtils {

    public static Object cast(Object origin, Class<?> type)
    {
        log.debug("cast: origin: {}", origin);
        log.debug("cast: type: {}", type);
        if(origin == null) return null;
        Class<?> aClass = origin.getClass();
        if(type.isAssignableFrom(aClass))
        {
            log.debug("cast: assingable {}->{}", aClass, type);
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
                if(componentType.isPrimitive() || componentType.getPackageName().startsWith("java"))
                {
                    Array.set(resultArray, i, Array.get(origin, i));
                }
                else{
                    Object o = cast(Array.get(origin, i), componentType);
                    Array.set(resultArray, i,o );
                }
            }

            return resultArray;
        }


        // 过来的数据为对象类型时
        if(origin instanceof HashMap map)
        {
            JSONObject jsonObject = new JSONObject(map);
            return jsonObject.toJavaObject(type);
        }

        if(origin instanceof JSONObject jsonObject){
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

    public static Object castGenericType( Object rpcData, Class<?>  type, Type genericReturnType )
    {

        log.debug("type: {}", type);
        log.debug("rpcData: {}", rpcData);
        log.debug("genericType: {}", genericReturnType);

        if(rpcData instanceof Map map)
        {
            if(Map.class.isAssignableFrom(type))
            {
                log.debug("map-> map");
                Map resultMap = new HashMap();
                if(genericReturnType instanceof ParameterizedType parameterizedType){
                    Type[] types =parameterizedType.getActualTypeArguments();
                    Class<?> keyType = (Class<?>) types[0];
                    Class<?> valueType = (Class<?>) types[1];
                    log.debug("keyType: {}, valueType: {}",keyType, valueType);

                    map.forEach((k,v)->{
                        Object key = cast(k, keyType);
                        Object value = cast(v, valueType);
                        resultMap.put(key, value);
                    });
                }

                return resultMap;
            }

            if(rpcData instanceof JSONObject jsonObject)
            {
                log.debug("JSONObject -> pojo");
                return jsonObject.toJavaObject(genericReturnType);
            }
            else if(!Map.class.isAssignableFrom(type))
            {
                log.debug("map -> pojo");
                return new JSONObject(map).toJavaObject(type);
            }
            else {
                log.debug("map -> ?");
                return rpcData;
            }
        }
        else if(rpcData instanceof List list)
        {
            Object[] rpcDataArr = list.toArray();
            if(type.isArray())
            {
                log.debug("list -> array");
                Class<?> componentType = type.getComponentType();
                Object resultArray = Array.newInstance(componentType, rpcDataArr.length);

                for (int i = 0;i < rpcDataArr.length; i++) {
                    if(componentType.isPrimitive() || componentType.getPackageName().startsWith("java"))
                    {
                        log.debug("ComponentType is primitive or package name start with java");
                        Array.set(resultArray, i, rpcDataArr[i]);
                    }
                    else {
                        log.debug("ComponentType is object");
                        Object vla = cast(rpcDataArr[i], componentType);
                        Array.set(resultArray, i, rpcDataArr[i]);
                    }
                }
                return resultArray;
            }
            else if(List.class.isAssignableFrom(type)){
                log.debug("list -> list");

                List resList = new ArrayList(rpcDataArr.length);
                if(genericReturnType instanceof ParameterizedType parameterizedType)
                {
                    Class<?> actualType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                    for (Object o : rpcDataArr) {
                        resList.add(cast(o, actualType));
                    }
                }
                else {
                    log.debug("genericReturnType {}", genericReturnType);
                    resList.addAll(list);
                }

                return resList;
            }else{
                log.warn("");
                return null;
            }
        }else{
            return cast(rpcData, type);
        }
    }


    @Nullable
    public static   Object castMethodReturnType(Method method, Object rpcData) {

        log.debug(" castMethodReturnType method: {}",method);
        log.debug("castMethodReturnType rpcData: {}", rpcData);
        Type genericReturnType = method.getGenericReturnType();
        Class<?> returnType = method.getReturnType();
        return castGenericType(rpcData,returnType, genericReturnType);
    }
}
