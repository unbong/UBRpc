package io.unbong.ubrpc.core.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
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
        if(origin == null) return null;
        Class<?> aClass = origin.getClass();
        if(type.isAssignableFrom(aClass))
        {
//            if(origin instanceof List list)
//            {
//                Type originaGenericType = aClass.getGenericSuperclass();
//
//                Type genericType = type.getGenericSuperclass();
//                if(genericType instanceof ParameterizedType parameterizedType)
//                {
//                    Type rawType =parameterizedType.getRawType();
//                    log.debug("type" + rawType);
//                }
//
//            }
//
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


    @Nullable
    public static   Object castMethodReturnType(Method method, Object rpcData) {


        Class<?> returnType = method.getReturnType();
        log.debug("---> method.getReturnType : "+returnType.toString());
        if(rpcData instanceof JSONObject jsonResult)
        {
            if(Map.class.isAssignableFrom(returnType))
            {
                Map resultMap = new HashMap();
                Type genericReturnType = method.getGenericReturnType();
                log.debug("method.getGenericReturnType :" + genericReturnType);

                if(genericReturnType instanceof ParameterizedType parameterizedType){
                    Class<?> keyType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                    Class<?> valueType =(Class<?>) parameterizedType.getActualTypeArguments()[1];
                    log.debug("keyType : "+ keyType);
                    log.debug("valueType: " + valueType);
                    jsonResult.entrySet().stream().forEach(
                            e->{
                                Object key = TypeUtils.cast(e.getKey(), keyType);
                                Object value = TypeUtils.cast(e.getValue(), valueType);
                                resultMap.put(key, value);
                            }
                    );
                    return resultMap;
                }

            }
            JSONObject data = (JSONObject) rpcData;
            return data.toJavaObject(method.getReturnType());
        }else if(rpcData instanceof JSONArray jsonArray)
        {
            // 数组类型的反序列化操作
            Object[] array = jsonArray.toArray();
            if(returnType.isArray()){
                // 返回数组的类型 仅仅是类型
                Class<?> componentType = returnType.getComponentType();

                Object resultArray  = Array.newInstance(componentType, array.length);
                for (int i = 0; i < array.length; i++) {
                    if(componentType.isPrimitive() || componentType.getPackageName().startsWith("java"))
                    {
                        Array.set(resultArray, i, array[i]);
                    }
                    else
                    {
                        Object castObject = cast(array[i], componentType);
                        Array.set(resultArray,i, castObject);
                    }

                }
                return resultArray;
            }
            else if(List.class.isAssignableFrom(returnType))
            {
                List<Object> resultList = new ArrayList<>();
                Type genericReturnType = method.getGenericReturnType();
                log.debug("genericType: " + genericReturnType);
                if(genericReturnType instanceof ParameterizedType parameterizedType){
                    Type actualType = parameterizedType.getActualTypeArguments()[0];
                    log.debug("actualType: "+actualType);
                    for(Object o: array){
                        resultList.add(cast(o, (Class<?>)actualType));
                    }
                }
                else{
                    resultList.addAll(Arrays.asList(array));
                }
                return resultList;
            }
        }

        // ub JSONObject to
        return TypeUtils.cast(rpcData, method.getReturnType());
    }
}
