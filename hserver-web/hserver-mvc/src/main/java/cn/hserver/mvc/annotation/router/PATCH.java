package cn.hserver.mvc.annotation.router;

import java.lang.annotation.*;


/**
 * @author hxm
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PATCH {
    String value() default "";

}
