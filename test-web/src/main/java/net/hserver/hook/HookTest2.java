package net.hserver.hook;

import net.hserver.log.Log;
import net.hserver.service.HelloService;
import top.hserver.core.interfaces.HookAdapter;
import top.hserver.core.ioc.annotation.Autowired;
import top.hserver.core.ioc.annotation.Hook;

import java.lang.reflect.Method;

@Hook(value = Log.class)
public class HookTest2 implements HookAdapter {

    @Autowired
    private HelloService helloService;

    @Override
    public void before(Class clazz, Method method, Object[] objects) {
        System.out.println("aop.-前置拦截 {}"+method.getName());
    }

    @Override
    public Object after(Class clazz, Method method,Object object) {
        System.out.println("aop.-后置拦截 {}"+object);
        return object;
    }

    @Override
    public void throwable(Class clazz, Method method, Throwable throwable) {
        System.out.println(throwable);

    }
}
