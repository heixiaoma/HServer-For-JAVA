package top.hserver.core.ioc.annotation.nacos;

import java.lang.annotation.*;

/**
 * @author hxm
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NacosValue {
    String dataId();

    String group() default "DEFAULT_GROUP";
}
