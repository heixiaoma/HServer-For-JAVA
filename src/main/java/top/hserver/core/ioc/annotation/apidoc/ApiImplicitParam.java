  package top.hserver.core.ioc.annotation.apidoc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiImplicitParam {
    //字段名字
    String name() default "";
    //字段描述
    String value() default "";
    //是否必填
    boolean required() default false;
    //数据类型
    DataType dataType() default DataType.String;
}
