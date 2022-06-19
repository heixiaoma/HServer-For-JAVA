package net.hserver.core.ioc.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Request
public @interface QpsLimit {
    int qps();
}
