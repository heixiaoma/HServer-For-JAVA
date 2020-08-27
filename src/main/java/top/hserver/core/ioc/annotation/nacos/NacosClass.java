package top.hserver.core.ioc.annotation.nacos;

import java.lang.annotation.*;

/**
 * @author hxm
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NacosClass {
    String dataId();

    String group() default "DEFAULT_GROUP";
}
