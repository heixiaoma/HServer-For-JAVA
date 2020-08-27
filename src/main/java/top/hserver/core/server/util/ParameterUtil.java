package top.hserver.core.server.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import top.hserver.core.interfaces.HttpRequest;
import top.hserver.core.interfaces.HttpResponse;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import top.hserver.core.server.context.ConstConfig;
import top.hserver.core.server.context.HServerContext;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
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
                Map<String, String> requestParams = hServerContext.getRequest().getRequestParams();
                try {
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
                        case "java.lang.String":
                            objects[i] = requestParams.get(typeName);
                            break;
                        default:
                            //不是基础类型可能就是我来转换的类型，哈哈，有毒哦
                            objects[i] = ConstConfig.OBJECT_MAPPER.convertValue(requestParams, parameterType.getType());
                            break;
                    }
                } catch (Exception e) {
                    objects[i] = null;
                }
            }
        }
        return objects;
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
            return new String[]{};
        }
    }


    public static void addParam(Class cs, Method method) {
        String[] paramNames = getParamNames(method);
        if (PARAM_NAME_MAP.containsKey(cs)) {
            ConcurrentHashMap<Method, String[]> concurrentHashMap = PARAM_NAME_MAP.get(cs);
            concurrentHashMap.put(method, paramNames);
            PARAM_NAME_MAP.put(cs, concurrentHashMap);
        } else {
            ConcurrentHashMap<Method, String[]> concurrentHashMap = new ConcurrentHashMap<>();
            concurrentHashMap.put(method, paramNames);
            PARAM_NAME_MAP.put(cs, concurrentHashMap);
        }

    }


    public static Object convert(Field field, String res) {
        if (res == null) {
            return null;
        }
        Object object = null;
        try {
            switch (field.getType().getName()) {
                case "int":
                case "java.lang.Integer":
                    object = Integer.parseInt(res);
                    break;

                case "double":
                case "java.lang.Double":
                    object = Double.parseDouble(res);
                    break;

                case "long":
                case "java.lang.Long":
                    object = Long.parseLong(res);
                    break;

                case "short":
                case "java.lang.java.lang.Short":
                    object = Short.parseShort(res);
                    break;

                case "float":
                case "java.lang.Float":
                    object = Float.parseFloat(res);
                    break;

                case "boolean":
                case "java.lang.Boolean":
                    object = Boolean.valueOf(res);
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
