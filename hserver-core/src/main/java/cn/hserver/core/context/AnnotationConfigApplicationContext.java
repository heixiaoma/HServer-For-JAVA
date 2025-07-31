package cn.hserver.core.context;

import cn.hserver.core.ioc.bean.BeanDefinition;
import cn.hserver.core.ioc.BeanFactory;
import cn.hserver.core.aop.HookFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class AnnotationConfigApplicationContext {

    private final static BeanFactory beanFactory = new BeanFactory();

    private final HookFactory hookFactory = new HookFactory();

    private final Map<String, BeanDefinition> beanDefinitions = new HashMap<>();

    public AnnotationConfigApplicationContext(Set<String> basePackages) {
        // 扫描
        basePackages.forEach(this::scan);
        //处理aop、hook关系
        hookFactory.handlerHookData(beanDefinitions);
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
            AnnotationFactory.processClass(clazz, beanDefinitions);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Error loading class: " + className, e);
        }
    }


    private void refresh() {
        // 注册所有Bean定义
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitions.entrySet()) {
            beanFactory.registerBeanDefinition(entry.getKey(), entry.getValue());
        }
        //单例bean的提前预处理
        // 新增：启动时预初始化所有单例Bean（处理依赖关系）
        for (String beanName : beanDefinitions.keySet()) {
            BeanDefinition beanDefinition = beanDefinitions.get(beanName);
            // 仅预初始化单例（默认作用域为singleton，若未指定Scope则视为singleton）
            if (beanDefinition.isSingleton()) {
                try {
                    // 调用getBean触发实例化和依赖注入，结果会缓存到beanFactory中
                    beanFactory.getBean(beanName);
                } catch (Exception e) {
                    throw new RuntimeException("初始化单例Bean失败：" + beanName, e);
                }
            }
        }
        System.out.println("<UNK>Bean<UNK>");
    }


    public static Object getBean(String name) throws Exception {
        try {
            return beanFactory.getBean(name);
        }catch (Exception e){
            return null;
        }
    }

    public static  <T> T getBean(Class<T> requiredType) throws Exception {
        try {
            return beanFactory.getBean(requiredType);
        }catch (Exception e){
            return null;
        }
    }
}