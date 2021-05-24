package top.hserver.core.server.util;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import javassist.*;
import top.hserver.core.interfaces.HttpRequest;
import top.hserver.core.interfaces.HttpResponse;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import top.hserver.core.server.context.ConstConfig;
import top.hserver.core.server.context.HServerContext;
import top.hserver.core.server.context.MimeType;
import top.hserver.core.server.exception.ValidateException;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 参数自动转换工具类
 *
 * @author hxm
 */
public class ParameterUtil {

    /**
     * 需要这map在初始化就被赋值了
     */
    private final static ConcurrentHashMap<Class, ConcurrentHashMap<Method, String[]>> PARAM_NAME_MAP = new ConcurrentHashMap<>();

    public static Object[] getMethodArgs(Class cs, Method method, HServerContext hServerContext) throws Exception {

        Parameter[] parameterTypes = method.getParameters();
        if (parameterTypes.length == 0) {
            return null;
        }
        Object[] objects = new Object[parameterTypes.length];
        String[] strings = PARAM_NAME_MAP.get(cs).get(method);
        if (parameterTypes.length != strings.length) {
            throw new Exception(method.getName() + "-方法参数获取异常");
        }

        for (int i = 0; i < parameterTypes.length; i++) {
            //构建方法参数
            if (parameterTypes[i].getParameterizedType() == HttpRequest.class) {
                objects[i] = hServerContext.getRequest();
            } else if (parameterTypes[i].getParameterizedType() == HttpResponse.class) {
                objects[i] = hServerContext.getResponse();
            } else {
                Parameter parameterType = parameterTypes[i];
                //更具基础类型转换
                String typeName = strings[i];
                Map<String, List<String>> requestParams = hServerContext.getRequest().getRequestParams();
                try {
                    Object convert = convert(parameterType.getType(), requestParams.get(typeName) == null ? null : requestParams.get(typeName).get(0));
                    if (convert != null) {
                        objects[i] = convert;
                    } else {
                        //不是基础类型可能就是我来转换的类型，哈哈，有毒哦
                        String type = hServerContext.getRequest().getHeader(HttpHeaderNames.CONTENT_TYPE.toString());
                        if (type != null && type.contains(MimeType.get("json"))) {
                            //payload
                            String rawData = hServerContext.getRequest().getRawData();
                            if (rawData != null) {
                                objects[i] = ConstConfig.OBJECT_MAPPER.readValue(rawData, parameterType.getType());
                            }
                        } else if (requestParams.size() > 0) {
                            //正常的表单
                            objects[i] = ConstConfig.OBJECT_MAPPER.convertValue(invokeData(requestParams), parameterType.getType());
                        }
                        //参数校验工具
                        ValidateUtil.validate(objects[i]);
                    }
                } catch (Exception e) {
                    if (e instanceof ValidateException) {
                        throw e;
                    }
                }
            }
        }
        return objects;
    }


    private static Map<String, String> invokeData(Map<String, List<String>> requestParams) {
        Map<String, String> data = new ConcurrentHashMap<>();
        requestParams.forEach((k, v) -> {
            if (k != null && v.size() > 0) {
                data.put(k, v.get(0));
            }
        });
        return data;
    }

    /**
     * 获取参数类型的名字
     *
     * @param method
     * @return
     */
    private static String[] getParamNames(Method method) {
        try {
            Class<?> clazz = method.getDeclaringClass();
            ClassPool pool = ClassPool.getDefault();
            CtClass clz = pool.get(clazz.getName());
            clz.freeze();
            clz.defrost();
            CtClass[] params = new CtClass[method.getParameterTypes().length];
            for (int i = 0; i < method.getParameterTypes().length; i++) {
                ClassClassPath classPath = new ClassClassPath(method.getParameterTypes()[i]);
                pool.insertClassPath(classPath);
                params[i] = pool.getCtClass(method.getParameterTypes()[i].getName());
            }
            CtMethod cm = clz.getDeclaredMethod(method.getName(), params);
            MethodInfo methodInfo = cm.getMethodInfo();
            CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
            LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute
                    .getAttribute(LocalVariableAttribute.tag);
            int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;
            String[] paramNames = new String[cm.getParameterTypes().length];
            for (int i = 0; i < attr.tableLength(); i++) {
                if (attr.index(i) >= pos && attr.index(i) < paramNames.length + pos) {
                    paramNames[attr.index(i) - pos] = attr.variableName(i);
                }
            }
            return paramNames;
        } catch (NotFoundException e) {
            e.printStackTrace();
            return new String[]{};
        }
    }


    public static void addParam(Class cs, Method method) throws Exception {
        String[] paramNames = getParamNames(method);
        if (method.getParameterTypes().length == paramNames.length) {
            if (PARAM_NAME_MAP.containsKey(cs)) {
                ConcurrentHashMap<Method, String[]> concurrentHashMap = PARAM_NAME_MAP.get(cs);
                concurrentHashMap.put(method, paramNames);
                PARAM_NAME_MAP.put(cs, concurrentHashMap);
            } else {
                ConcurrentHashMap<Method, String[]> concurrentHashMap = new ConcurrentHashMap<>();
                concurrentHashMap.put(method, paramNames);
                PARAM_NAME_MAP.put(cs, concurrentHashMap);
            }
        } else {
            throw new Exception("参数异常");
        }
    }


    public static Object convert(Class<?> type, String res) {
        Object object = null;
        try {
            switch (type.getName()) {
                case "int":
                    if (res == null) {
                        object = 0;
                    } else {
                        object = Integer.parseInt(res);
                    }
                    break;
                case "java.lang.Integer":
                    object = Integer.parseInt(res);
                    break;

                case "double":
                    if (res == null) {
                        object = 0.0;
                    } else {
                        object = Double.parseDouble(res);
                    }
                    break;
                case "java.lang.Double":
                    object = Double.parseDouble(res);
                    break;

                case "long":
                    if (res == null) {
                        object = 0L;
                    } else {
                        object = Long.parseLong(res);
                    }
                    break;
                case "java.lang.Long":
                    object = Long.parseLong(res);
                    break;
                case "short":
                    if (res == null) {
                        object = 0;
                    } else {
                        object = Short.parseShort(res);
                    }
                    break;
                case "java.lang.Short":
                    object = Short.parseShort(res);
                    break;
                case "float":
                    if (res == null) {
                        object = 0;
                    } else {
                        object = Float.parseFloat(res);
                    }
                    break;
                case "java.lang.Float":
                    object = Float.parseFloat(res);
                    break;
                case "boolean":
                    if (res == null) {
                        object = false;
                    } else {
                        object = Boolean.parseBoolean(res);
                    }
                    break;
                case "java.lang.Boolean":
                    object = Boolean.parseBoolean(res);
                    break;
                case "byte":
                    if (res == null) {
                        object = false;
                    } else {
                        object = Byte.parseByte(res);
                    }
                    break;
                case "java.lang.Byte":
                    object = Byte.parseByte(res);
                    break;

                case "java.lang.BigInteger":
                    object = BigInteger.valueOf(Long.parseLong(res));
                    break;

                case "java.lang.BigDecimal":
                    object = BigDecimal.valueOf(Long.parseLong(res));
                    break;

                case "java.lang.String":
                    object = res;
                    break;
                default:
                    return null;
            }
        } catch (Exception ignored) {
        }
        return object;
    }

}
