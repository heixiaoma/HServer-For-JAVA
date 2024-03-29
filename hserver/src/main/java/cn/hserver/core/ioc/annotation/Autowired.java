package cn.hserver.core.ioc.annotation;

import java.lang.annotation.*;


/**
 * @author hxm
 */
@Target({ElementType.TYPE,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autowired {
    String value() default "";
}
