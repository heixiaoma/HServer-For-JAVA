package top.hserver.core.ioc.annotation.validate;

import java.lang.annotation.*;

/**
 * 字段 CharSequence 集合 map 数组必须在这范围内
 *
 * @author hxm
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Size {

    long min();

    long max();

    String message() default "字段不在设置的min-max的范围内";
}
