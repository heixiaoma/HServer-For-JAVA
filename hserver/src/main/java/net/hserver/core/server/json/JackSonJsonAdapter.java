package net.hserver.core.server.json;

import com.fasterxml.jackson.databind.JavaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;
import net.hserver.core.server.context.ConstConfig;
import net.hserver.core.server.util.ExceptionUtil;

import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author hxm
 */
public class JackSonJsonAdapter implements JsonAdapter {

    private static final Logger log = LoggerFactory.getLogger(JackSonJsonAdapter.class);

    @Override
    public Object convertObject(String data, Parameter type) {
        try {
            if (Collection.class.isAssignableFrom(type.getType()) || Map.class.isAssignableFrom(type.getType())) {
                return ConstConfig.OBJECT_MAPPER.readValue(data, getParameterizedTypeImplType((ParameterizedType) type.getParameterizedType()));
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
            log.error(ExceptionUtil.getMessage(e));
            return null;
        }
    }

    private Class<?> getTypeClass(Type rawType) {
        if (rawType instanceof ParameterizedTypeImpl) {
            return ((ParameterizedTypeImpl) rawType).getRawType();
        } else {
            return (Class<?>) rawType;
        }
    }

    private JavaType getParameterizedTypeImplType(ParameterizedType parameterizedType) {
        Type rawType = parameterizedType.getRawType();
        Class<?> typeClass = getTypeClass(rawType);
        if (Collection.class.isAssignableFrom(typeClass)) {
            Type listActualTypeArgument = parameterizedType.getActualTypeArguments()[0];
            if (listActualTypeArgument instanceof ParameterizedType) {
                return ConstConfig.OBJECT_MAPPER.getTypeFactory().constructParametricType(List.class, getParameterizedTypeImplType((ParameterizedType) listActualTypeArgument));
            } else {
                return ConstConfig.OBJECT_MAPPER.getTypeFactory().constructParametricType(List.class, getTypeClass(parameterizedType.getActualTypeArguments()[0]));
            }
        } else if (Map.class.isAssignableFrom(typeClass)) {
            Type mapActualTypeArgument = parameterizedType.getActualTypeArguments()[1];
            if (mapActualTypeArgument instanceof ParameterizedType) {
                JavaType keyType = ConstConfig.OBJECT_MAPPER.getTypeFactory().constructType(getTypeClass(parameterizedType.getActualTypeArguments()[0]));
                return ConstConfig.OBJECT_MAPPER.getTypeFactory().constructParametricType(Map.class, keyType, getParameterizedTypeImplType((ParameterizedType) mapActualTypeArgument));

            } else {
                return ConstConfig.OBJECT_MAPPER.getTypeFactory().constructParametricType(Map.class, getTypeClass(parameterizedType.getActualTypeArguments()[0]), getTypeClass(parameterizedType.getActualTypeArguments()[1]));
            }
        }
        return null;
    }
}