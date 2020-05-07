package top.test.annotation;

import top.hserver.core.interfaces.AnnotationAdapter;
import top.hserver.core.ioc.annotation.Bean;

import java.lang.annotation.Annotation;

/**
 * 自定方法级别注解的总入口
 */
@Bean
public class MyAutoAnnotationAdapter implements AnnotationAdapter {

    @Override
    public void before(Annotation annotation, Object[] args, Class clazz) {

        System.out.println(annotation);
        System.out.println(args.length);
        if (args.length > 0) {
            System.out.println(args[0]);
        }
        System.out.println(clazz);
    }

    @Override
    public void after(Annotation annotation, Object object, Class clazz) {
        System.out.println(object);
    }
}
