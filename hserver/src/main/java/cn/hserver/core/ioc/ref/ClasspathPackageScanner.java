package cn.hserver.core.ioc.ref;

import cn.hserver.core.ioc.annotation.*;
import cn.hserver.core.ioc.annotation.queue.QueueListener;
import cn.hserver.core.server.util.ClassLoadUtil;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.*;

/**
 * @author hxm
 */
public class ClasspathPackageScanner implements PackageScanner {
    private final Map<Class, Set<Class<?>>> annotationClass = new HashMap<>();

    /**
     * 初始化
     *
     * @param packageNames
     */
    public ClasspathPackageScanner(Set<String> packageNames) {
        packageNames.forEach(basePackage -> {
            List<Class<?>> classes = ClassLoadUtil.LoadClasses(basePackage, false);
           a: for (Class<?> aClass : classes) {
                Annotation[] annotations = aClass.getAnnotations();
                for (Annotation annotation : annotations) {
                    //不再处理HServerType类型，这里有可能时外部定义的注解，我们只好兼容对方
//                    if (annotation.annotationType().getAnnotation(HServerType.class)!=null) {
//                    }
                    add(aClass,annotation.annotationType());
                    continue a;
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
