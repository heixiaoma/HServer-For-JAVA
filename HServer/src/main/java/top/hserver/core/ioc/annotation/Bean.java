package top.hserver.core.ioc.annotation;

import java.lang.annotation.*;


/**
 * @author hxm
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Bean {
    String value() default "";
}