package test1.annotation;


import top.hserver.core.ioc.annotation.Auto;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Auto
public @interface Log {
}
