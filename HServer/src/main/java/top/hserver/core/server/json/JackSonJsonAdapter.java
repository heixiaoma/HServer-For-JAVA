package top.hserver.core.server.json;

import com.fasterxml.jackson.databind.JavaType;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;
import top.hserver.core.server.context.ConstConfig;

import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author hxm
 */
public class JackSonJsonAdapter implements JsonAdapter {


    /**
     * 常见对象转换，不支持 List<Map> 复杂类型和套娃
     *
     * </>
     * @param data
     * @param type
     * @return
     */
    @Override
    public Object convertObject(String data, Parameter type) {
        try {
            if (Collection.class.isAssignableFrom(type.getType()) || Map.class.isAssignableFrom(type.getType())) {
                return ConstConfig.OBJECT_MAPPER.readValue(data, getCollectionType(type));
            } else {
                return ConstConfig.OBJECT_MAPPER.readValue(data, type.getType());
            }
        } catch (Throwable e) {
            return null;
        }
    }

    @Override
    public Object convertObject(String data, Class type) {
        try {
            return ConstConfig.OBJECT_MAPPER.readValue(data, type);
        } catch (Throwable e) {
            return null;
        }
    }

    @Override
    public Object convertMapToObject(Map data, Class type) {
        return ConstConfig.OBJECT_MAPPER.convertValue(data, type);
    }

    @Override
    public String convertString(Object data) {
        try {
            return ConstConfig.OBJECT_MAPPER.writeValueAsString(data);
        } catch (Exception e) {
            return null;
        }
    }

    private JavaType getCollectionType(Parameter parameter) {
        ParameterizedType parameterizedType = (ParameterizedType) parameter.getParameterizedType();
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        Class[] classes = new Class[actualTypeArguments.length];
        for (int i = 0; i < actualTypeArguments.length; i++) {
            if (actualTypeArguments[i] instanceof ParameterizedTypeImpl) {
                classes[i] = ((ParameterizedTypeImpl) actualTypeArguments[i]).getRawType();
            } else {
                //if (actualTypeArguments[i] instanceof Class)
                classes[i] = ((Class) actualTypeArguments[i]);
            }
        }
        return ConstConfig.OBJECT_MAPPER.getTypeFactory().constructParametricType(parameter.getType(), classes);
    }

}
