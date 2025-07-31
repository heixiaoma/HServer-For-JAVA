package cn.hserver.core.context.handler;

import cn.hserver.core.aop.annotation.Hook;
import cn.hserver.core.ioc.bean.BeanDefinition;

import java.util.Map;

public class HookHandler implements AnnotationHandler {
    @Override
    public void handle(Class<?> clazz, Map<String, BeanDefinition> beanDefinitions) {
        String className = clazz.getName();
        if (clazz.isAnnotationPresent(Hook.class)) {
            // 注册配置类本身作为Bean
            BeanDefinition configBeanDef = new BeanDefinition();
            configBeanDef.setBeanClass(clazz);
            String configBeanName = className.substring(className.lastIndexOf('.') + 1);
            configBeanName = Character.toLowerCase(configBeanName.charAt(0)) + configBeanName.substring(1);
            beanDefinitions.put(configBeanName, configBeanDef);
        }
    }
}
