package top.hserver.core.ioc.annotation;

import java.lang.annotation.*;


/**
 * @author hxm
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PUT {
    String value();
}