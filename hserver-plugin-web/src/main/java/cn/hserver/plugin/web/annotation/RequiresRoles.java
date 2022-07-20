package cn.hserver.plugin.web.annotation;

import cn.hserver.core.ioc.annotation.HServerBoot;
import cn.hserver.core.ioc.annotation.HServerType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 角色判断注解
 * @author hxm
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@HServerType
public @interface RequiresRoles {

    String[] value();

    /**
     * 多个权限的逻辑操作，是and还是or，默认是and
     */
    Logical logical() default Logical.AND;
}
