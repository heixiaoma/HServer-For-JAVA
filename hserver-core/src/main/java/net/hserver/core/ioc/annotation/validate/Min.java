package net.hserver.core.ioc.annotation.validate;

import java.lang.annotation.*;

/**
 * 字段值必须小于这个值，number
 *
 * @author hxm
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Min {

    long value();

    String message() default "字段值必须小于设置的值";
}
