package net.hserver.core.ioc.ref;

import net.hserver.core.ioc.annotation.*;
import net.hserver.core.ioc.annotation.queue.QueueListener;
import net.hserver.core.server.util.ClassLoadUtil;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.*;

/**
 * @author hxm
 */
public class ClasspathPackageScanner implements PackageScanner {
    private Map<Class, Set<Class<?>>> annotationClass = new HashMap<>();

    /**
     * 初始化
     *
     * @param packageNames
     */
    public ClasspathPackageScanner(Set<String> packageNames) {
        packageNames.forEach(basePackage -> {
            List<Class<?>> classes = ClassLoadUtil.LoadClasses(basePackage, false);
            for (Class<?> aClass : classes) {
                //类级别的注解
                if (aClass.getAnnotation(Bean.class) != null) {
                    add(aClass, Bean.class);
                }
                if (aClass.getAnnotation(WebSocket.class) != null) {
                    add(aClass, WebSocket.class);
                }
                if (aClass.getAnnotation(Configuration.class) != null) {
                    add(aClass, Configuration.class);
                }
                if (aClass.getAnnotation(Controller.class) != null) {
                    add(aClass, Controller.class);
                }
                if (aClass.getAnnotation(Hook.class) != null) {
                    add(aClass, Hook.class);
                }
                if (aClass.getAnnotation(QueueListener.class) != null) {
                    add(aClass, QueueListener.class);
                }
                if (aClass.getAnnotation(ConfigurationProperties.class) != null) {
                    add(aClass, ConfigurationProperties.class);
                }
                //单元测试模式。存在就加载
                try {
                    Class<Annotation> aClass1 = (Class<Annotation>) this.getClass().getClassLoader().loadClass("org.junit.runner.RunWith");
                    if (aClass.getAnnotation(aClass1) != null) {
                        add(aClass, aClass1);
                    }
                } catch (Exception ignored) {
                }
            }
        });
    }

    private <A extends Annotation> void add(Class<?> aClass, Class<A> annotation) {
        Set<Class<?>> classes = annotationClass.get(annotation);
        if (classes == null) {
            classes = new HashSet<>();
            classes.add(aClass);
            annotationClass.put(annotation, classes);
        } else {
            classes.add(aClass);
            annotationClass.put(annotation, classes);
        }
    }

    @Override
    public <A extends Annotation> Set<Class<?>> getAnnotationList(Class<A> annotation) throws IOException {
        Set<Class<?>> classes = annotationClass.get(annotation);
        if (classes == null) {
            return new HashSet<>();
        }
        return classes;
    }
}