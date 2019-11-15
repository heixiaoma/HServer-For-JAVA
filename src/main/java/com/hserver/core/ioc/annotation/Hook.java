package com.hserver.core.ioc.annotation;

import java.lang.annotation.*;
import java.lang.reflect.Method;


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Hook {
    Class value();

    String method();
}