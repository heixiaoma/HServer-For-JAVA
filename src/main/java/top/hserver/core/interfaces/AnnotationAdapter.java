package top.hserver.core.interfaces;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public interface AnnotationAdapter {

  void before(Annotation annotation, Object[] args, Class clazz, Method method);

  void after(Annotation annotation, Object returnResult, Class clazz, Method method);
}
