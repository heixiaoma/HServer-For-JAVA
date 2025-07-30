package cn.hserver.core.context.handler;

import cn.hserver.core.ioc.annotation.Component;
import cn.hserver.core.ioc.annotation.Scope;
import cn.hserver.core.ioc.bean.BeanDefinition;

import java.util.Map;

public class ComponentHandler implements AnnotationHandler {
    @Override
    public void handle(Class<?> clazz, Map<String, BeanDefinition> beanDefinitions) {
        String className = clazz.getName();
        if (clazz.isAnnotationPresent(Component.class)) {
            Component component = clazz.getAnnotation(Component.class);
            String beanName = component.value();
            if (beanName.isEmpty()) {
                beanName = className.substring(className.lastIndexOf('.') + 1);
                beanName = Character.toLowerCase(beanName.charAt(0)) + beanName.substring(1);
            }
            BeanDefinition beanDefinition = new BeanDefinition();
            beanDefinition.setBeanClass(clazz);
            // 处理作用域
            if (clazz.isAnnotationPresent(Scope.class)) {
                Scope scope = clazz.getAnnotation(Scope.class);
                beanDefinition.setScope(scope.value());
            }
            beanDefinitions.put(beanName, beanDefinition);
        }
    }
}
