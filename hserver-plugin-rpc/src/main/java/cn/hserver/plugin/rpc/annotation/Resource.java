package cn.hserver.plugin.rpc.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Resource {
    String value() default "";

    //服务名
    String serverName();

    //组名
    String groupName() default "DEFAULT_GROUP";
}
