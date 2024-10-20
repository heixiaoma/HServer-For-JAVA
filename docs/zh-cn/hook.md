
## Hook操作

提供hook注解，它只能Hook在ioc中存在的bean对象. hook功能除了hook指定的类所有方法，还能hook注解，只要包含这个注解的类都会被hook.

```java
import cn.hserver.core.interfaces.HookAdapter;
import cn.hserver.core.ioc.annotation.Autowired;
import cn.hserver.core.ioc.annotation.Hook;
import test1.service.HelloService;
import test1.service.Test;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * hook 指定的Test类，在调用Test的所有方法 都会进入before 和 after，可以通过Method 选择处理或者不处理.
 */
@Slf4j
@Hook(Test.class)
public class HookTest implements HookAdapter {

    @Autowired
    private HelloService helloService;

    @Override
    public void before(Class clazz, Method method, Object[] objects) {
        log.debug("aop.-前置拦截111111111111111111111");
    }

    @Override
    public Object after(Class clazz, Method method,Object object) {
        return object + "aop-后置拦截1111111111111111"+helloService.sayHello();
    }

    @Override
    public void throwable(Class clazz, Method method, Throwable throwable) {
        System.out.println(throwable);
    }
}

```



```java
import lombok.extern.slf4j.Slf4j;
import test1.log.Log;
import test1.service.HelloService;
import test1.service.Test;
import top.hserver.core.interfaces.HookAdapter;
import top.hserver.core.ioc.annotation.*;
import java.lang.reflect.Method;
/**
 * hook 指定用了@Log的类，只要用的@Log的类都会被Hook住,或者作用在方法得注解也会生效。这个功能主要用途在做一些 自定义注解时比较常用.比如做一个@log 日志打印注解 或者 耗时统计注解.
 */
@Slf4j
@Hook(value = Log.class)
public class HookTest2 implements HookAdapter {

    @Autowired
    private HelloService helloService;

    @Override
    public void before(Class clazz, Method method, Object[] objects) {
        log.debug("aop.-前置拦截 {}",method.getName());
    }

    @Override
    public Object after(Class clazz, Method method,Object object) {
        log.debug("aop.-后置拦截 {}",object);
        return object;
    }

    @Override
    public void throwable(Class clazz, Method method, Throwable throwable) {
        System.out.println(throwable);

    }
}
```

```java
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {
    String value() default "";
}
```


上面测试例子都是HServer Test包里的Test1文件中，有兴趣的可以去运行体验哈