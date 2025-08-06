package cn.hserver.mvc.annotation.validate;

import java.lang.annotation.*;

/**
 * 字段CharSequence 类型的长度必须是 length 长
 *
 * @author hxm
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Length {

    long value();

    String message() default "字段长度不满足条件";
}
