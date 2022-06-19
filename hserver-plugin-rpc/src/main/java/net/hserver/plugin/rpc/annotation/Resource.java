package net.hserver.plugin.rpc.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Resource {
    String value() default "";

    String serverName();
}
