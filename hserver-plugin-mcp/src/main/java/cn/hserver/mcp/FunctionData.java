package cn.hserver.mcp;

import cn.hserver.core.ioc.IocUtil;
import cn.hserver.mcp.annotation.Param;
import cn.hserver.modelcontextprotocol.spec.McpSchema;
import cn.hserver.plugin.web.context.WebConstConfig;
import cn.hserver.plugin.web.util.ParameterUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FunctionData {


    private Object object;

    private Class<?> aClass;

    private Method method;

    private Class<?> returnType;

    private String[] argumentNames;

    public FunctionData(Class<?> aClass, Method method) {
        this.aClass = aClass;
        this.method = method;
        this.argumentNames = ParameterUtil.getParamNames(method);
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
            throw new RuntimeException("参数类型错误");
        }
    }

    public McpSchema.JsonSchema getInputSchema() {
        McpSchema.JsonSchema jsonSchema = new McpSchema.JsonSchema();
        jsonSchema.setType("object");
        if (argumentNames != null) {
            jsonSchema.setRequired(Arrays.asList(argumentNames));
            Parameter[] parameters = this.method.getParameters();
            Map<String, Object> p = new HashMap<>();
            for (int i = 0; i < parameters.length; i++) {
                Param annotation = parameters[i].getAnnotation(Param.class);
                if (annotation==null){
                    throw new RuntimeException("缺少Param注解描述");
                }
                Map<String, Object> data = data(annotation.type().name(), annotation.description(), annotation.defaultValue(), annotation.enums());
                p.put(this.argumentNames[i],data);
            }
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
            data[i] = WebConstConfig.JSONADAPTER.convertObjToObject(args.get(this.argumentNames[i]), parameters[i].getType());
        }
        return data;
    }


    public McpSchema.CallToolResult invoke(Map<String, Object> args) {
        McpSchema.CallToolResult res = new McpSchema.CallToolResult();
        try {
            if (this.object == null) {
                this.object = IocUtil.getBean(aClass);
            }
            Object invoke = method.invoke(object, genArgs(args));
            if (invoke instanceof McpSchema.Content) {
                List<McpSchema.Content> data = new ArrayList<>();
                data.add((McpSchema.Content) invoke);
                res.setContent(data);
            } else if (invoke instanceof List) {
                res.setContent((List<McpSchema.Content>) invoke);
            } else {
                res.setIsError(true);
                res.setContent(new ArrayList<>());
            }
        } catch (Exception e1) {
            res.setIsError(true);
            res.setContent(new ArrayList<>());
        }
        return res;
    }


    public Map<String,Object> data(String type,String description,String defaultStr,String[] enums){
        Map<String,Object> data=new HashMap<>();
        data.put("type",type);
        if (description!=null&&!description.isEmpty()) {
            data.put("description", description);
        }
        if (defaultStr!=null&& !defaultStr.trim().isEmpty()) {
            data.put("default", defaultStr);
        }
        if(enums!=null&& enums.length>0) {
            data.put("enum", enums);
        }
        return data;
    }


}
