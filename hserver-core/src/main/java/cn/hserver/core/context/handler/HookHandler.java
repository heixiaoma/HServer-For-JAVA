package cn.hserver.core.context.handler;

import cn.hserver.core.aop.annotation.Hook;
import cn.hserver.core.ioc.bean.BeanDefinition;

import java.util.Map;

public class HookHandler implements AnnotationHandler {
    @Override
    public void handle(Class<?> clazz, Map<String, BeanDefinition> beanDefinitions) {
        if (clazz.isAnnotationPresent(Hook.class)) {
            defaultHandler(clazz, beanDefinitions);
        }
    }
}
