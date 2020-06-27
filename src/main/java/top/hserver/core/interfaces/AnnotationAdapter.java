package top.hserver.core.interfaces;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author hxm
 */
public interface AnnotationAdapter {

  /**
   * 方法之前
   * @param annotation
   * @param args
   * @param clazz
   * @param method
   */
  void before(Annotation annotation, Object[] args, Class clazz, Method method);

  /**
   * 方法之后
   * @param annotation
   * @param returnResult
   * @param clazz
   * @param method
   */
  void after(Annotation annotation, Object returnResult, Class clazz, Method method);
}
