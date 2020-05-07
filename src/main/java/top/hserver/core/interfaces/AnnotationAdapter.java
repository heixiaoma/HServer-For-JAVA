package top.hserver.core.interfaces;

import java.lang.annotation.Annotation;

public interface AnnotationAdapter {

    void before(Annotation annotation, Object[] args, Class clazz);

    void after(Annotation annotation, Object returnResult, Class clazz);
}
