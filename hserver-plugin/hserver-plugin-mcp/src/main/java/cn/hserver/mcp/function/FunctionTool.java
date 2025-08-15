package cn.hserver.mcp.function;

import cn.hserver.core.context.IocApplicationContext;
import cn.hserver.mcp.ObjConvertUtil;
import cn.hserver.mcp.annotation.Param;
import cn.hserver.mcp.type.McpType;
import cn.hserver.modelcontextprotocol.spec.McpSchema;
import cn.hserver.mvc.util.ParameterUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FunctionTool {


    private Object object;

    private Class<?> aClass;

    private Method method;

    private Class<?> returnType;

    private String[] argumentNames;

    public FunctionTool(Class<?> aClass, Method method) {
        this.aClass = aClass;
        this.method = method;
        this.argumentNames = ParameterUtil.getMethodsParamNames(method);
        Class<?> returnType1 = method.getReturnType();
        if (Collection.class.isAssignableFrom(returnType1)) {
            try {
                Type returnType = method.getGenericReturnType();
                // 判断返回类型是否为 List
                if (returnType instanceof ParameterizedType) {
                    ParameterizedType pt = (ParameterizedType) returnType;
                    // 获取 List 的实际泛型类型
                    Type[] typeArguments = pt.getActualTypeArguments();
                    Type typeArgument = typeArguments[0];
                    Class<?> genericClass = (Class<?>) typeArgument;
                    if (McpSchema.Content.class.isAssignableFrom(genericClass)) {
                        this.returnType = returnType1;
                    }
                }
            } catch (Exception ignored) {
            }
        } else if (McpSchema.Content.class.isAssignableFrom(returnType1)) {
            this.returnType = returnType1;
        }
        if (this.returnType == null) {
            throw new RuntimeException("参数类型错误,必须是 McpSchema.Content 类型");
        }
    }

    public McpSchema.JsonSchema getInputSchema() {
        McpSchema.JsonSchema jsonSchema = new McpSchema.JsonSchema();
        jsonSchema.setType("object");
        if (argumentNames != null) {
            List<String> names = new ArrayList<>();
            Parameter[] parameters = this.method.getParameters();
            Map<String, Object> p = new HashMap<>();
            for (int i = 0; i < parameters.length; i++) {
                Param annotation = parameters[i].getAnnotation(Param.class);
                Map<String, Object> data;
                if (annotation == null) {
                    names.add(argumentNames[i]);
                    data = data(McpType.string.name(), null, null, null);
                } else {
                    if (annotation.required()){
                        names.add(argumentNames[i]);
                    }
                    data = data(annotation.type().name(), annotation.description(), annotation.defaultValue(), annotation.enums());
                }
                p.put(this.argumentNames[i], data);
            }
            jsonSchema.setRequired(names);
            jsonSchema.setProperties(p);
        }
        return jsonSchema;
    }


    private Object[] genArgs(Map<String, Object> args) {
        Parameter[] parameters = this.method.getParameters();
        if (parameters.length == 0) {
            return null;
        }
        Object[] data = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            data[i] = ObjConvertUtil.convert(parameters[i].getType(),args.get(this.argumentNames[i]));
        }
        return data;
    }


    public McpSchema.CallToolResult invoke(Map<String, Object> args) {
        McpSchema.CallToolResult res = new McpSchema.CallToolResult();
        List<McpSchema.Content> data = new ArrayList<>();
        try {
            if (this.object == null) {
                this.object = IocApplicationContext.getBean(aClass);
            }
            Object invoke = method.invoke(object, genArgs(args));
            if (invoke instanceof McpSchema.Content) {
                data.add((McpSchema.Content) invoke);
                res.setContent(data);
            } else if (invoke instanceof List) {
                data.addAll((List<McpSchema.Content>) invoke);
                res.setContent(data);
            } else {
                res.setIsError(true);
                res.setContent(data);
            }
        } catch (Exception e1) {
            log.error(e1.getMessage(), e1);
            res.setIsError(true);
            res.setContent(data);
        }
        return res;
    }


    public Map<String, Object> data(String type, String description, String defaultStr, String[] enums) {
        Map<String, Object> data = new HashMap<>();
        data.put("type", type);
        if (description != null && !description.isEmpty()) {
            data.put("description", description);
        }
        if (defaultStr != null && !defaultStr.trim().isEmpty()) {
            data.put("default", defaultStr);
        }
        if (enums != null && enums.length > 0) {
            data.put("enum", enums);
        }
        return data;
    }


}
