package cn.hserver.plugin.beetlsql.annotation;

import cn.hserver.core.ioc.annotation.HServerType;

import java.lang.annotation.*;

/**
 * @author hxm
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@HServerType
@Documented
public @interface BeetlSQL {
    String value() default "";
}
