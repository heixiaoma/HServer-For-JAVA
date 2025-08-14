package cn.hserver.mcp;

import cn.hserver.core.context.handler.AnnotationHandler;
import cn.hserver.core.ioc.bean.BeanDefinition;
import cn.hserver.mcp.annotation.McpServerEndpoint;

import java.util.Map;

public class McpAnnotationHandler implements AnnotationHandler {
    @Override
    public void handle(Class<?> clazz, Map<String, BeanDefinition> beanDefinitions) {
        if (clazz.isAnnotationPresent(McpServerEndpoint.class)) {
            defaultHandler(clazz, beanDefinitions);
        }
    }
}
