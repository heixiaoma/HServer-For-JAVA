package cn.hserver.mvc.annotation.validate;

import java.lang.annotation.*;

/**
 * 字段为必须为true
 *
 * @author hxm
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AssertTrue {
    String message() default "字段不是:True";
}
