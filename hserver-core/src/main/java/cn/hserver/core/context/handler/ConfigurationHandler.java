package cn.hserver.core.context.handler;

import cn.hserver.core.config.annotation.Configuration;
import cn.hserver.core.config.annotation.ConfigurationProperties;
import cn.hserver.core.ioc.annotation.Bean;
import cn.hserver.core.ioc.annotation.Scope;
import cn.hserver.core.ioc.bean.BeanDefinition;

import java.lang.reflect.Method;
import java.util.Map;

public class ConfigurationHandler implements AnnotationHandler {

    @Override
    public void handle(Class<?> clazz, Map<String, BeanDefinition> beanDefinitions) {
        String className = clazz.getName();
        if (clazz.isAnnotationPresent(Configuration.class)) {
            Configuration config = clazz.getAnnotation(Configuration.class);
            String configBeanName = config.value();
            if (configBeanName.isEmpty()) {
                configBeanName = className.substring(className.lastIndexOf('.') + 1);
                configBeanName = Character.toLowerCase(configBeanName.charAt(0)) + configBeanName.substring(1);
            }
            // 注册配置类本身作为Bean
            BeanDefinition configBeanDef = new BeanDefinition();
            configBeanDef.setBeanClass(clazz);
            beanDefinitions.put(configBeanName, configBeanDef);
            // 处理@Bean注解的方法
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Bean.class)) {
                    Bean bean = method.getAnnotation(Bean.class);
                    String beanName = bean.value();
                    if (beanName.isEmpty()) {
                        beanName = method.getName();
                    }

                    BeanDefinition beanDef = new BeanDefinition();
                    beanDef.setBeanClass(method.getReturnType());
                    beanDef.setFactoryBeanName(configBeanName);
                    beanDef.setFactoryMethod(method);

                    // 处理@Scope注解
                    if (method.isAnnotationPresent(Scope.class)) {
                        Scope scope = method.getAnnotation(Scope.class);
                        beanDef.setScope(scope.value());
                    }

                    beanDefinitions.put(beanName, beanDef);
                }
            }
        }

        if (clazz.isAnnotationPresent(ConfigurationProperties.class)) {
            String configBeanName= className.substring(className.lastIndexOf('.') + 1);
                configBeanName = Character.toLowerCase(configBeanName.charAt(0)) + configBeanName.substring(1);
            // 注册配置类本身作为Bean
            BeanDefinition configBeanDef = new BeanDefinition();
            configBeanDef.setBeanClass(clazz);
            beanDefinitions.put(configBeanName, configBeanDef);
        }
    }
}
