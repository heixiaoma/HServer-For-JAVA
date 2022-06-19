package net.hserver.core.ioc.annotation.validate;

import java.lang.annotation.*;

/**
 * 字段CharSequence 必须满足这个正则
 *
 * @author hxm
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Pattern {

    String value();

    String message() default "字段不满足正则表达式";
}
