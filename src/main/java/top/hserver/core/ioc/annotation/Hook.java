package top.hserver.core.ioc.annotation;

import java.lang.annotation.*;
import java.lang.reflect.Method;


/**
 * @author hxm
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Hook {
    Class[] value();
}