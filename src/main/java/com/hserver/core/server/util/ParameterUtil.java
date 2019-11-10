package com.hserver.core.server.util;

import com.hserver.core.server.context.Request;
import com.hserver.core.server.context.Response;
import com.hserver.core.server.context.WebContext;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.Parameter;
import java.util.Map;

/**
 * 参数自动转换工具类
 */
public class ParameterUtil {

    public static Object[] getMethodArgs(Parameter[] parameterTypes, WebContext webContext) throws Exception {
        Object[] objects = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            //构建方法参数
            if (parameterTypes[i].getParameterizedType() == Request.class) {
                objects[i] = webContext.getRequest();
            } else if (parameterTypes[i].getParameterizedType() == Response.class) {
                objects[i] = webContext.getResponse();
            } else {
                Parameter parameterType = parameterTypes[i];
                //更具基础类型转换
                String typeName = parameterType.getName();
                Map<String, String> requestParams = webContext.getRequest().getRequestParams();
                if (requestParams.get(typeName) == null) {
                    objects[i] = null;
                    continue;
                }
                switch (parameterType.getType().getName()) {
                    case "int":
                    case "java.lang.Integer":
                        objects[i] = Integer.parseInt(requestParams.get(typeName));
                        break;

                    case "double":
                    case "java.lang.Double":
                        objects[i] = Double.parseDouble(requestParams.get(typeName));
                        break;

                    case "long":
                    case "java.lang.Long":
                        objects[i] = Long.parseLong(requestParams.get(typeName));
                        break;

                    case "short":
                    case "java.lang.java.lang.Short":
                        objects[i] = Short.parseShort(requestParams.get(typeName));
                        break;

                    case "float":
                    case "java.lang.Float":
                        objects[i] = Float.parseFloat(requestParams.get(typeName));
                        break;

                    case "boolean":
                    case "java.lang.Boolean":
                        objects[i] = Boolean.valueOf(requestParams.get(typeName));
                        break;
                    default:
                        objects[i] = requestParams.get(typeName);
                }

            }
        }
        return objects;
    }

}
