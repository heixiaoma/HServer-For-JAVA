package cn.hserver.core.context.handler;

import cn.hserver.core.ioc.bean.BeanDefinition;
import org.junit.runner.RunWith;

import java.util.Map;

public class TestRunWithHandler implements AnnotationHandler {
    @Override
    public void handle(Class<?> clazz, Map<String, BeanDefinition> beanDefinitions) {
        if (clazz.isAnnotationPresent(RunWith.class)) {
            defaultHandler(clazz, beanDefinitions);
        }
    }
}
