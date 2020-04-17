package top.hserver.core.ioc.ref;

import top.hserver.core.ioc.annotation.*;
import top.hserver.core.ioc.annotation.event.EventHandler;
import top.hserver.core.server.util.ClassLoadUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

@Slf4j
public class ClasspathPackageScanner implements PackageScanner {

  private Map<Class, List<Class<?>>> annotationClass = new HashMap<>();

  /**
   * 初始化
   *
   * @param basePackage
   */
  public ClasspathPackageScanner(String basePackage) {
    List<Class<?>> classes = ClassLoadUtil.LoadClasses(basePackage, true);
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
      if (aClass.getAnnotation(Filter.class) != null) {
        add(aClass, Filter.class);
      }
      if (aClass.getAnnotation(EventHandler.class) != null) {
        add(aClass, EventHandler.class);
      }
    }

  }

  private <A extends Annotation> void add(Class<?> aClass, Class<A> annotation) {
    List<Class<?>> classes = annotationClass.get(annotation);
    if (classes == null) {
      classes = new ArrayList<>();
      classes.add(aClass);
      annotationClass.put(annotation, classes);
    } else {
      classes.add(aClass);
      annotationClass.put(annotation, classes);
    }
  }

  @Override
  public <A extends Annotation> List<Class<?>> getAnnotationList(Class<A> annotation) throws IOException {
    List<Class<?>> classes = annotationClass.get(annotation);
    if (classes == null) {
      return new ArrayList<>();
    }
    return classes;
  }
}