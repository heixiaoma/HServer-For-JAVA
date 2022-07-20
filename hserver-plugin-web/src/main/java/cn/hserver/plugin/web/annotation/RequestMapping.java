package cn.hserver.plugin.web.annotation;


import cn.hserver.core.ioc.annotation.RequestMethod;

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
