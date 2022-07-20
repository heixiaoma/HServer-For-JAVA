package cn.hserver.plugin.web.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Request
public @interface QpsLimit {
    int qps();
}
