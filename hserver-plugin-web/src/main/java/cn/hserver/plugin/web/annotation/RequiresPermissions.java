package cn.hserver.plugin.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限判断注解
 * @author hxm
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresPermissions {

    String[] value();

    /**
     * 多个权限的逻辑操作，是and还是or，默认是and
     */
    Logical logical() default Logical.AND;

    /**
     * 描述，一般用于生成菜单用
     * @return
     */
    String name() default "";
}
