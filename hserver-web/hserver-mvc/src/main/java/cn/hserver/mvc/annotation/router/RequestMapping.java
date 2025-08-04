package cn.hserver.mvc.annotation.router;



import cn.hserver.mvc.constants.HttpMethod;

import java.lang.annotation.*;

/**
 * @author hxm
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {
  String value() default "";
  HttpMethod[] method() default {};
}
