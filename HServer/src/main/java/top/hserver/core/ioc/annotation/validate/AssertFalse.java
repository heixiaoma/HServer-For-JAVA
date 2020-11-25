package top.hserver.core.ioc.annotation.validate;

import java.lang.annotation.*;

/**
 * 字段为必须为false
 *
 * @author hxm
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AssertFalse {
    String message() default "字段不是:False";
}
