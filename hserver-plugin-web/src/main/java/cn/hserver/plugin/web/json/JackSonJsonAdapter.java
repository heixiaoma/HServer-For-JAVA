package cn.hserver.plugin.web.json;

import cn.hserver.plugin.web.context.WebConstConfig;
import com.fasterxml.jackson.databind.JavaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.hserver.core.server.util.ExceptionUtil;

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
                return WebConstConfig.OBJECT_MAPPER.readValue(data, getParameterizedTypeImplType((ParameterizedType) type.getParameterizedType()));
            } else {
                return WebConstConfig.OBJECT_MAPPER.readValue(data, type.getType());
            }
        } catch (Throwable e) {
            return null;
        }
    }

    @Override
    public Object convertObject(String data, Class type) {
        try {
            return WebConstConfig.OBJECT_MAPPER.readValue(data, type);
        } catch (Throwable e) {
            return null;
        }
    }

    @Override
    public Object convertMapToObject(Map data, Class type) {
        return WebConstConfig.OBJECT_MAPPER.convertValue(data, type);
    }

    @Override
    public String convertString(Object data) {
        try {
            return WebConstConfig.OBJECT_MAPPER.writeValueAsString(data);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return null;
        }
    }

    private Class<?> getTypeClass(Type rawType) {
        if (rawType instanceof ParameterizedType) {
            //todo jdk 升级跳转 待测试
            return (Class<?>) ((ParameterizedType) rawType).getRawType();
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
                return WebConstConfig.OBJECT_MAPPER.getTypeFactory().constructParametricType(List.class, getParameterizedTypeImplType((ParameterizedType) listActualTypeArgument));
            } else {
                return WebConstConfig.OBJECT_MAPPER.getTypeFactory().constructParametricType(List.class, getTypeClass(parameterizedType.getActualTypeArguments()[0]));
            }
        } else if (Map.class.isAssignableFrom(typeClass)) {
            Type mapActualTypeArgument = parameterizedType.getActualTypeArguments()[1];
            if (mapActualTypeArgument instanceof ParameterizedType) {
                JavaType keyType = WebConstConfig.OBJECT_MAPPER.getTypeFactory().constructType(getTypeClass(parameterizedType.getActualTypeArguments()[0]));
                return WebConstConfig.OBJECT_MAPPER.getTypeFactory().constructParametricType(Map.class, keyType, getParameterizedTypeImplType((ParameterizedType) mapActualTypeArgument));

            } else {
                return WebConstConfig.OBJECT_MAPPER.getTypeFactory().constructParametricType(Map.class, getTypeClass(parameterizedType.getActualTypeArguments()[0]), getTypeClass(parameterizedType.getActualTypeArguments()[1]));
            }
        }
        return null;
    }
}
