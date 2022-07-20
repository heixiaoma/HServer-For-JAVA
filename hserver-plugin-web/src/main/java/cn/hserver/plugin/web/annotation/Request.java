package cn.hserver.plugin.web.annotation;

import cn.hserver.core.ioc.annotation.HServerBoot;

import java.lang.annotation.*;

/**
 * @author hxm
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Request {
}
