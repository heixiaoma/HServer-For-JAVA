package top.hserver.core.ioc.annotation.validate;

import java.lang.annotation.*;

/**
 * CharSequence 集合 map 数组 不是null 长度或者size 大于0
 *
 * @author hxm
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NotEmpty {
    String message() default "字段不能为空,长度或者Size必须大于0 ";
}
