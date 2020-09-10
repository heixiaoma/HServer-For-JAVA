package test1.hook;

import lombok.extern.slf4j.Slf4j;
import test1.log.Log;
import test1.service.HelloService;
import test1.service.Test;
import top.hserver.core.interfaces.HookAdapter;
import top.hserver.core.ioc.annotation.*;
import java.lang.reflect.Method;

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
}
