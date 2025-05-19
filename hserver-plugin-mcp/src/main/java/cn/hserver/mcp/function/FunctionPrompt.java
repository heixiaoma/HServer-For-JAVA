package cn.hserver.mcp.function;

import cn.hserver.core.ioc.IocUtil;
import cn.hserver.mcp.ObjConvertUtil;
import cn.hserver.mcp.annotation.Param;
import cn.hserver.mcp.type.McpType;
import cn.hserver.modelcontextprotocol.spec.McpSchema;
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
public class FunctionPrompt {


    private Object object;

    private Class<?> aClass;

    private Method method;

    private Class<?> returnType;

    private String[] argumentNames;

    public FunctionPrompt(Class<?> aClass, Method method) {
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
                    if (McpSchema.PromptMessage.class.isAssignableFrom(genericClass)) {
                        this.returnType = returnType1;
                    }
                }
            } catch (Exception ignored) {
            }
        } else if (McpSchema.PromptMessage.class.isAssignableFrom(returnType1)) {
            this.returnType = returnType1;
        }
        if (this.returnType == null) {
            throw new RuntimeException("参数类型错误,必须是 McpSchema.PromptMessage 类型");
        }
    }

    public List<McpSchema.PromptArgument> getPromptArguments() {
        List<McpSchema.PromptArgument> promptArguments = new ArrayList<>();
        if (argumentNames != null) {
            Parameter[] parameters = this.method.getParameters();
            Map<String, Object> p = new HashMap<>();
            for (int i = 0; i < parameters.length; i++) {
                Param annotation = parameters[i].getAnnotation(Param.class);
                if (annotation == null) {
                    promptArguments.add(new McpSchema.PromptArgument(argumentNames[i],null, true));
                } else {
                    promptArguments.add(new McpSchema.PromptArgument(argumentNames[i],annotation.description(), annotation.required()));
                }
            }
        }
        return promptArguments;
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


    public McpSchema.GetPromptResult invoke(McpSchema.GetPromptRequest request, McpSchema.Prompt prompt) {
        McpSchema.GetPromptResult getPromptResult = new McpSchema.GetPromptResult();
        getPromptResult.setDescription(prompt.getDescription());
        List<McpSchema.PromptMessage> promptMessages = new ArrayList<>();
        McpSchema.CallToolResult res = new McpSchema.CallToolResult();
        try {
            if (this.object == null) {
                this.object = IocUtil.getBean(aClass);
            }
            Object invoke = method.invoke(object, genArgs(request.getArguments()));
            if (invoke instanceof McpSchema.PromptMessage) {
                promptMessages.add((McpSchema.PromptMessage) invoke);
            } else if (invoke instanceof List) {
                promptMessages.addAll((List<McpSchema.PromptMessage>) invoke);
            }
        } catch (Exception e1) {
            res.setIsError(true);
            res.setContent(new ArrayList<>());
        }
        getPromptResult.setMessages(promptMessages);
        return getPromptResult;
    }


}
