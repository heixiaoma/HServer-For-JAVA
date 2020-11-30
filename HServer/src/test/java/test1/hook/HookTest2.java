package test1.hook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test1.log.Log;
import test1.service.HelloService;
import top.hserver.core.interfaces.HookAdapter;
import top.hserver.core.ioc.annotation.*;

import java.lang.reflect.Method;

@Hook(value = Log.class)
public class HookTest2 implements HookAdapter {

    private static final Logger log = LoggerFactory.getLogger(HookTest2.class);

    @Autowired
    private HelloService helloService;

    @Override
    public void before(Class clazz, Method method, Object[] objects) {
        log.debug("aop.-前置拦截 {}", method.getName());
    }

    @Override
    public Object after(Class clazz, Method method, Object object) {
        log.debug("aop.-后置拦截 {}", object);
        return object;
    }

    @Override
    public void throwable(Class clazz, Method method, Throwable throwable) {
        System.out.println(throwable);

    }
}
