package cn.hserver.plugin.web.util;

import cn.hserver.core.server.util.ExceptionUtil;
import cn.hserver.core.server.util.ObjConvertUtil;
import cn.hserver.plugin.web.context.HServerContext;
import cn.hserver.plugin.web.context.MimeType;
import cn.hserver.plugin.web.context.WebConstConfig;
import cn.hserver.plugin.web.exception.ValidateException;
import cn.hserver.plugin.web.interfaces.HttpRequest;
import cn.hserver.plugin.web.interfaces.HttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import javassist.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

import java.lang.reflect.*;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
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
     * 需要这map在初始化就被赋值了
     */
    private final static ConcurrentHashMap<Class, ConcurrentHashMap<Method, ControllerParameter>> PARAM_NAME_MAP = new ConcurrentHashMap<>();

    public static Object[] getMethodArgs(Class cs, Method method, HServerContext hServerContext) throws Exception {
        Parameter[] parameterTypes = method.getParameters();
        if (parameterTypes.length == 0) {
            return null;
        }
        Object[] objects = new Object[parameterTypes.length];
        ControllerParameter controllerParameter = PARAM_NAME_MAP.get(cs).get(method);
        String[] strings = controllerParameter.getName();
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
                    Object convert = ObjConvertUtil.convert(parameterType.getType(), requestParams.get(typeName) == null ? null : requestParams.get(typeName).get(0));
                    if (convert != null) {
                        objects[i] = convert;
                    } else {
                        //不是基础类型可能就是我来转换的类型，哈哈，有毒哦
                        String type = hServerContext.getRequest().getHeader(HttpHeaderNames.CONTENT_TYPE.toString());
                        if (type != null && type.contains(MimeType.get("json"))) {
                            //payload
                            String rawData = hServerContext.getRequest().getRawData();
                            if (rawData != null) {
                                objects[i] = WebConstConfig.JSONADAPTER.convertObject(rawData, parameterType);
                            }
                        } else if (requestParams.size() > 0) {
                            //正常的表单
                            objects[i] = WebConstConfig.JSONADAPTER.convertMapToObject(invokeData(requestParams), parameterType.getType());
                        }
                        //参数校验工具
                        if (controllerParameter.isValidate) {
                            ValidateUtil.validate(objects[i]);
                        }
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


    public static void addParam(Class cs, Method method) throws Exception {
        String[] paramNames = getParamNames(method);
        boolean validate = ValidateUtil.isValidate(method);
        if (method.getParameterTypes().length == paramNames.length) {
            if (PARAM_NAME_MAP.containsKey(cs)) {
                ConcurrentHashMap<Method, ParameterUtil.ControllerParameter> concurrentHashMap = PARAM_NAME_MAP.get(cs);
                concurrentHashMap.put(method, new ControllerParameter(paramNames, validate));
                PARAM_NAME_MAP.put(cs, concurrentHashMap);
            } else {
                ConcurrentHashMap<Method, ParameterUtil.ControllerParameter> concurrentHashMap = new ConcurrentHashMap<>();
                concurrentHashMap.put(method, new ControllerParameter(paramNames, validate));
                PARAM_NAME_MAP.put(cs, concurrentHashMap);
            }
        } else {
            throw new Exception("参数异常");
        }
    }



    public static class ControllerParameter {
        private final String[] name;
        private final boolean isValidate;

        public ControllerParameter(String[] name, boolean isValidate) {
            this.name = name;
            this.isValidate = isValidate;
        }

        public String[] getName() {
            return name;
        }

        public boolean isValidate() {
            return isValidate;
        }
    }


}
