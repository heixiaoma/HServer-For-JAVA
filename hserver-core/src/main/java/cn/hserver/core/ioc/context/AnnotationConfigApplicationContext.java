package cn.hserver.core.ioc.context;

import cn.hserver.core.aop.annotation.Hook;
import cn.hserver.core.ioc.annotation.Bean;
import cn.hserver.core.ioc.annotation.Component;
import cn.hserver.core.ioc.annotation.Configuration;
import cn.hserver.core.ioc.annotation.Scope;
import cn.hserver.core.ioc.bean.BeanDefinition;
import cn.hserver.core.ioc.bean.DefaultListableBeanFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AnnotationConfigApplicationContext {

    private final DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

    private final Map<String, BeanDefinition> beanDefinitions = new HashMap<>();

    public AnnotationConfigApplicationContext(String basePackage) {
        // 注册后处理器
        scan(basePackage);
        refresh();
    }

    private void scan(String basePackage) {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            String path = basePackage.replace('.', '/');
            Enumeration<URL> resources = classLoader.getResources(path);

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                File directory = new File(resource.getFile());
                if (directory.exists()) {
                    scanDirectory(directory, basePackage);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error scanning package: " + basePackage, e);
        }
    }

    private void scanDirectory(File directory, String basePackage) {
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isDirectory()) {
                scanDirectory(file, basePackage + "." + file.getName());
            } else if (file.getName().endsWith(".class")) {
                String className = basePackage + '.' + file.getName().substring(0, file.getName().length() - 6);
                processClass(className);
            }
        }
    }

    private void processClass(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            // 处理@Component注解
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
            // 新增：处理@Configuration注解
            else if (clazz.isAnnotationPresent(Configuration.class)) {
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
            } else if (clazz.isAnnotationPresent(Hook.class)) {
                // 注册配置类本身作为Bean
                BeanDefinition configBeanDef = new BeanDefinition();
                configBeanDef.setBeanClass(clazz);
                String configBeanName = className.substring(className.lastIndexOf('.') + 1);
                configBeanName = Character.toLowerCase(configBeanName.charAt(0)) + configBeanName.substring(1);
                beanDefinitions.put(configBeanName, configBeanDef);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Error loading class: " + className, e);
        }
    }


    private void refresh() {
        // 注册所有Bean定义
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitions.entrySet()) {
            beanFactory.registerBeanDefinition(entry.getKey(), entry.getValue());
        }
    }


    public Object getBean(String name) throws Exception {
        return beanFactory.getBean(name);
    }

    public <T> T getBean(Class<T> requiredType) throws Exception {
        return beanFactory.getBean(requiredType);
    }
}