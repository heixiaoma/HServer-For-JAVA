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
        if (clazz.isAnnotationPresent(Configuration.class)) {
            Configuration config = clazz.getAnnotation(Configuration.class);
            BeanDefinition configBeanDef = new BeanDefinition();
            configBeanDef.setBeanClass(clazz);
            String configBeanName = config.value();
            if (configBeanName.isEmpty()) {
                configBeanName = configBeanDef.getDefaultBeanName();
            }
            // 处理@Scope注解
            if (clazz.isAnnotationPresent(Scope.class)) {
                Scope scope = clazz.getAnnotation(Scope.class);
                configBeanDef.setScope(scope.value());
            }

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
                    if (method.getReturnType().getConstructors().length > 0) {
                        beanDef.setConstructor(method.getReturnType().getConstructors()[0]);
                    }else {
                        beanDef.setConstructor(null);
                    }
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
            // 注册配置类本身作为Bean
            BeanDefinition configBeanDef = new BeanDefinition();
            configBeanDef.setBeanClass(clazz);
            beanDefinitions.put(configBeanDef.getDefaultBeanName(), configBeanDef);
        }
    }
}
