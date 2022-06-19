package net.hserver.core.ioc.annotation;


import java.lang.annotation.*;

/**
 * @author hxm
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Request
public @interface RequestMapping {
  String value();

  RequestMethod[] method() default {};
}