package cn.hserver.mvc.util;


import cn.hserver.mvc.constants.MimeType;
import cn.hserver.mvc.constants.WebConstConfig;
import cn.hserver.mvc.context.WebContext;
import cn.hserver.mvc.request.Request;
import cn.hserver.mvc.response.Response;
import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 参数自动转换工具类
 *
 * @author hxm
 */
public class ParameterUtil {
    private static final Logger log = LoggerFactory.getLogger(ParameterUtil.class);
    /**
     * 获取参数类型的名字
     *
     * @param method
     * @return
     */
    public static String[] getMethodsParamNames(Method method) {
        try {
            Class<?> clazz = method.getDeclaringClass();
            ClassPool pool = ClassPool.getDefault();
            pool.appendClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));
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
            Map<Integer,String > kvMap = new HashMap<>();
            for (int i = 0; i < attr.tableLength(); i++) {
                kvMap.put(attr.index(i),attr.variableName(i));
            }
            List<Map.Entry<Integer, String>> entryList = new ArrayList<>(kvMap.entrySet());
            entryList.sort(Map.Entry.comparingByKey());
            for (int i = 0; i < paramNames.length; i++) {
                paramNames[i]=entryList.get(pos+i).getValue();
            }
            return paramNames;
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return new String[]{};
        }
    }

    public static Object[] getMethodArgs(Method method, String[] argsNames, WebContext ctx){
        if (argsNames.length == 0) {
            return null;
        }
        Parameter[] parameterTypes = method.getParameters();
        Object[] objects = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            //构建方法参数
            if (parameterTypes[i].getParameterizedType() == WebContext.class) {
                objects[i] = ctx;
            } else if (parameterTypes[i].getParameterizedType() == Response.class) {
                objects[i] = ctx.response;
            }  else if (parameterTypes[i].getParameterizedType() == Request.class) {
                objects[i] = ctx.request;
            }else {
                Parameter parameterType = parameterTypes[i];
                //更具基础类型转换
                String typeName = argsNames[i];
                Map<String, List<String>> requestParams = ctx.request.getRequestParams();
                try {
                    List<String> params = requestParams.get(typeName);
                    Object convert = ObjConvertUtil.convert(parameterType.getType(),  params==null ? null : params.get(0));
                    if (convert != null) {
                        objects[i] = convert;
                    } else {
                        String type = ctx.request.getHeader("content-type");
                        if (type != null && type.contains(MimeType.get("json"))) {
                            //payload
                            String rawData = ctx.request.getRawData();
                            if (rawData != null) {
                                objects[i] = WebConstConfig.JSONADAPTER.convertObject(rawData, parameterType);
                            }
                        } else if (!requestParams.isEmpty()) {
                            //正常的表单
                            objects[i] = WebConstConfig.JSONADAPTER.convertMapToObject(invokeData(requestParams), parameterType.getType());
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return objects;
    }


    private static Map<String, String> invokeData(Map<String, List<String>> requestParams) {
        Map<String, String> data = new ConcurrentHashMap<>();
        requestParams.forEach((k, v) -> {
            if (k != null && !v.isEmpty()) {
                data.put(k, v.get(0));
            }
        });
        return data;
    }
}
