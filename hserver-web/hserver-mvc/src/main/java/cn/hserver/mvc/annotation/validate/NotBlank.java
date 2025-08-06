package cn.hserver.mvc.annotation.validate;

import java.lang.annotation.*;

/**
 * 字段不能为null同时不是 ""
 *
 * @author hxm
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NotBlank {
    String message() default "字段不能为Null或者' '";
}
