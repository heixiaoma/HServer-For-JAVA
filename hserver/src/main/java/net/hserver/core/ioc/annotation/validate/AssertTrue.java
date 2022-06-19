package net.hserver.core.ioc.annotation.validate;

import java.lang.annotation.*;

/**
 * 字段为必须为true
 *
 * @author hxm
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AssertTrue {
    String message() default "字段不是:True";
}
