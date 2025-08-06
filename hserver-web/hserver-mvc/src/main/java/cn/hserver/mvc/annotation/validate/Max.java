package cn.hserver.mvc.annotation.validate;

import java.lang.annotation.*;

/**
 * 字段值必须大于这个值，number
 *
 * @author hxm
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Max {

    long value();

    String message() default "字段值必须大于设置的值";
}
