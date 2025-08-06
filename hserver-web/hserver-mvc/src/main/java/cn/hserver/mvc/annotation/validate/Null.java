package cn.hserver.mvc.annotation.validate;

import java.lang.annotation.*;

/**
 * 字段必须为Null
 *
 * @author hxm
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Null {

    String message() default "字段必须为null";
}
