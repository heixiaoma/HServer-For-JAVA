package cn.hserver.core.ioc.context;

import cn.hserver.core.ioc.annotation.Component;
import cn.hserver.core.ioc.annotation.Scope;
import cn.hserver.core.ioc.bean.BeanDefinition;
import cn.hserver.core.ioc.bean.DefaultListableBeanFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

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
        for (File file : directory.listFiles()) {
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
            if (clazz.isAnnotationPresent(Component.class)) {
                Component component = clazz.getAnnotation(Component.class);
                String beanName = component.value();
                if (beanName.isEmpty()) {
                    beanName = className.substring(className.lastIndexOf('.') + 1);
                    beanName = Character.toLowerCase(beanName.charAt(0)) + beanName.substring(1);
                }

                BeanDefinition beanDefinition = new BeanDefinition();
                beanDefinition.setBeanName(beanName);
                beanDefinition.setBeanClass(clazz);

                // 处理作用域
                if (clazz.isAnnotationPresent(Scope.class)) {
                    Scope scope = clazz.getAnnotation(Scope.class);
                    beanDefinition.setScope(scope.value());
                }

                beanDefinitions.put(beanName, beanDefinition);
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