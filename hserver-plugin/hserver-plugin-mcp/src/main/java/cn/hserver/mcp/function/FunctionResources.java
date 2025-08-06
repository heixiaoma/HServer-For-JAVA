package cn.hserver.mcp.function;

import cn.hserver.core.ioc.IocUtil;
import cn.hserver.mcp.ObjConvertUtil;
import cn.hserver.mcp.annotation.ResourcesMapping;
import cn.hserver.modelcontextprotocol.spec.McpSchema;
import cn.hserver.plugin.web.util.ParameterUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FunctionResources {
    private Object object;

    private Class<?> aClass;

    private Method method;

    private Class<?> returnType;

    private String[] argumentNames;

    public FunctionResources(Class<?> aClass, Method method) {
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
                    if (McpSchema.ResourceContents.class.isAssignableFrom(genericClass)) {
                        this.returnType = returnType1;
                    }
                }
            } catch (Exception ignored) {
            }
        } else if (McpSchema.ResourceContents.class.isAssignableFrom(returnType1)) {
            this.returnType = returnType1;
        }
        if (this.returnType == null) {
            throw new RuntimeException("参数类型错误,必须是 McpSchema.ResourceContents 类型");
        }
    }




    public static Map<String, String> parseString(String template, String actual) {
        Map<String, String> result = new HashMap<>();
        // 生成正则表达式模式和参数名称列表
        StringBuilder regexBuilder = new StringBuilder();
        java.util.List<String> paramNames = new java.util.ArrayList<>();
        // 模板中的参数占位符模式：{参数名}
        Pattern paramPattern = Pattern.compile("\\{([^}]+)}");
        Matcher matcher = paramPattern.matcher(template);
        int lastIndex = 0;
        while (matcher.find()) {
            // 添加固定部分到正则表达式
            regexBuilder.append(Pattern.quote(template.substring(lastIndex, matcher.start())));
            // 提取参数名
            String paramName = matcher.group(1);
            paramNames.add(paramName);

            // 添加参数捕获组到正则表达式
            regexBuilder.append("([^/]+)");

            lastIndex = matcher.end();
        }
        // 添加剩余部分
        regexBuilder.append(Pattern.quote(template.substring(lastIndex)));

        // 编译正则表达式
        Pattern regexPattern = Pattern.compile(regexBuilder.toString());
        Matcher regexMatcher = regexPattern.matcher(actual);

        // 提取参数值
        if (regexMatcher.matches()) {
            for (int i = 0; i < paramNames.size(); i++) {
                result.put(paramNames.get(i), regexMatcher.group(i + 1));
            }
        }

        return result;
    }


    private Object[] genArgs(Map<String, String> args) {
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

    public  McpSchema.ReadResourceResult invoke(McpSchema.ReadResourceRequest request, ResourcesMapping resourcesMapping) {
        McpSchema.ReadResourceResult readResourceResult = new McpSchema.ReadResourceResult();
        List<McpSchema.ResourceContents> resourceContentsList = new ArrayList<>();
        Map<String, String> stringStringMap = parseString(resourcesMapping.uri(), request.getUri());
        try {
            if (this.object == null) {
                this.object = IocUtil.getBean(aClass);
            }
            Object invoke = method.invoke(object, genArgs(stringStringMap));
            if (invoke instanceof Collection) {
                Collection<McpSchema.ResourceContents> data = (Collection<McpSchema.ResourceContents>) invoke;
                resourceContentsList.addAll(data);
            } else {
                resourceContentsList.add((McpSchema.ResourceContents)invoke);
            }
        }catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        readResourceResult.setContents(resourceContentsList);
        return readResourceResult;
    }

}
