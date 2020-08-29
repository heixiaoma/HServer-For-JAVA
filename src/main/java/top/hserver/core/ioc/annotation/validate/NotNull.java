package top.hserver.core.ioc.annotation.validate;

import java.lang.annotation.*;

/**
 * 字段不能为Null
 *
 * @author hxm
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NotNull {

    String message() default "字段不能为空";
}
