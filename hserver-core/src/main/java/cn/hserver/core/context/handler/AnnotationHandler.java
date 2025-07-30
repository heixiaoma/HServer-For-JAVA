package cn.hserver.core.context.handler;

import cn.hserver.core.ioc.bean.BeanDefinition;

import java.util.Map;

public interface AnnotationHandler {

    void handle(Class<?> clazz, Map<String, BeanDefinition> beanDefinitions);
}
