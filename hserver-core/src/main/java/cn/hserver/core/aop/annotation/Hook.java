package cn.hserver.core.aop.annotation;

import cn.hserver.core.ioc.annotation.Component;

import java.lang.annotation.*;


/**
 * @author hxm
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
@Documented
public @interface Hook {
    Class<?>[] value();
}
