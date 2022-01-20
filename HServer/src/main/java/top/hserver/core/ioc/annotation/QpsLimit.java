package top.hserver.core.ioc.annotation;

import top.hserver.core.interfaces.Limit;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Request
public @interface QpsLimit {
    int qps();
}
