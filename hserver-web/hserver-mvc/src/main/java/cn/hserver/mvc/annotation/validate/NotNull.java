package cn.hserver.mvc.annotation.validate;

import java.lang.annotation.*;

/**
 * 字段不能为Null
 *
 * @author hxm
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NotNull {

    String message() default "字段不能为空";
}
