package cn.hserver.plugin.web.annotation;


import java.lang.annotation.*;

/**
 * @author hxm
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Request
public @interface RequestMapping {
  String value() default "";


  RequestMethod[] method() default {};
}
