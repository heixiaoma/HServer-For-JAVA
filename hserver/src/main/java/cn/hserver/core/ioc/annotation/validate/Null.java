package cn.hserver.core.ioc.annotation.validate;

import java.lang.annotation.*;

/**
 * 字段必须为Null
 *
 * @author hxm
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Null {

    String message() default "字段必须为null";
}
