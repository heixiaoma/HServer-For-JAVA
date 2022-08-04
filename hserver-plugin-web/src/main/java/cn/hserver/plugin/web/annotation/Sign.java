package cn.hserver.plugin.web.annotation;

import cn.hserver.core.ioc.annotation.HServerBoot;
import cn.hserver.core.ioc.annotation.HServerType;

import java.lang.annotation.*;


/**
 * @author hxm
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@HServerType
public @interface Sign {
    String value() default "";
}
