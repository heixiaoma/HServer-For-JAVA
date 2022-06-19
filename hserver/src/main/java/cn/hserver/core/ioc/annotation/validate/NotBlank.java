package cn.hserver.core.ioc.annotation.validate;

import java.lang.annotation.*;

/**
 * 字段不能为null同时不是 ""
 *
 * @author hxm
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NotBlank {
    String message() default "字段不能为Null或者' '";
}
