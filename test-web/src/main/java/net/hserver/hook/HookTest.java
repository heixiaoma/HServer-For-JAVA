package net.hserver.hook;

import net.hserver.service.HelloService;
import net.hserver.service.Test;
import top.hserver.core.interfaces.HookAdapter;
import top.hserver.core.ioc.annotation.Autowired;
import top.hserver.core.ioc.annotation.Hook;

import java.lang.reflect.Method;

@Hook(Test.class)
public class HookTest implements HookAdapter {

    @Autowired
    private HelloService helloService;

    @Override
    public void before(Class clazz, Method method, Object[] objects) {
        System.out.println("aop.-前置拦截111111111111111111111");
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
