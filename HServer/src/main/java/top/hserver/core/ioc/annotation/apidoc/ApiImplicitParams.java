package top.hserver.core.ioc.annotation.apidoc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author hxm
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiImplicitParams {
  //字段描述
  ApiImplicitParam[] value();
  //描述
  String note();
  //接口名字
  String name();
}
