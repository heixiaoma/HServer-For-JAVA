package com.hserver.core.ioc.annotation;

import java.lang.annotation.*;


@Target({ElementType.TYPE,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface In {
    String value() default "";
}