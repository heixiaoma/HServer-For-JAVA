package com.hserver.core.server.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hserver.core.server.context.Request;
import com.hserver.core.server.context.Response;
import com.hserver.core.server.context.WebContext;
import com.test.action.Hello;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 参数自动转换工具类
 */
public class ParameterUtil {

    /**
     * 需要这map在初始化就被赋值了
     */
    private final static ConcurrentHashMap<Class, ConcurrentHashMap<Method, String[]>> paramNameMap = new ConcurrentHashMap<>();

    public static Object[] getMethodArgs(Class cs, Method method, WebContext webContext) throws Exception {
        Parameter[] parameterTypes = method.getParameters();
        Object[] objects = new Object[parameterTypes.length];

        String[] strings = paramNameMap.get(cs).get(method);
        if (parameterTypes.length != strings.length) {
            throw new Exception(method.getName() + "参数参数获取异常");
        }

        for (int i = 0; i < parameterTypes.length; i++) {
            //构建方法参数
            if (parameterTypes[i].getParameterizedType() == Request.class) {
                objects[i] = webContext.getRequest();
            } else if (parameterTypes[i].getParameterizedType() == Response.class) {
                objects[i] = webContext.getResponse();
            } else {
                Parameter parameterType = parameterTypes[i];
                //更具基础类型转换
                String typeName = strings[i];
                Map<String, String> requestParams = webContext.getRequest().getRequestParams();
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
                        JSONObject jsonObject = JSON.parseObject(JSON.toJSON(requestParams).toString());
                        objects[i] = JSON.toJavaObject(jsonObject, parameterType.getType());
                        break;
                }

            }
        }
        return objects;
    }


    /**
     * 获取参数类型的名字
     *
     * @param cs
     * @param method
     * @return
     */
    private static String[] getParamNames(Class cs, Method method) {
        try {
            ClassPool pool = ClassPool.getDefault();
            CtClass cc = pool.get(cs.getName());
            CtMethod cm = cc.getDeclaredMethod(method.getName());
            // 使用javaassist的反射方法获取方法的参数名
            MethodInfo methodInfo = cm.getMethodInfo();
            CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
            LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
            if (attr == null) {
                return null;
            }
            String[] paramNames = new String[cm.getParameterTypes().length];
            int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;
            for (int i = 0; i < paramNames.length; i++) {
                paramNames[i] = attr.variableName(i + pos);
            }
            return paramNames;

        } catch (NotFoundException e) {
            return new String[]{};
        }
    }


    public static void addParam(Class cs, Method method) {
        String[] paramNames = getParamNames(cs, method);
        if (paramNameMap.containsKey(cs)) {
            ConcurrentHashMap<Method, String[]> concurrentHashMap = paramNameMap.get(cs);
            concurrentHashMap.put(method, paramNames);
            paramNameMap.put(cs, concurrentHashMap);
        } else {
            ConcurrentHashMap<Method, String[]> concurrentHashMap = new ConcurrentHashMap<>();
            concurrentHashMap.put(method, paramNames);
            paramNameMap.put(cs, concurrentHashMap);
        }

    }


}
