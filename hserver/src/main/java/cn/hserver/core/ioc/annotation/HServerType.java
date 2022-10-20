package cn.hserver.core.ioc.annotation;

import java.lang.annotation.*;

/**
 * @author hxm
 * 标记一个类直接将放在待处理里 可以在扫描器里获取到
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HServerType {
}
