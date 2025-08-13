package cn.hserver.core.aop;

import cn.hserver.core.context.IocApplicationContext;
import cn.hserver.core.ioc.BeanFactory;
import cn.hserver.core.ioc.bean.BeanDefinition;
import javassist.util.proxy.MethodHandler;

import java.lang.reflect.Method;

public class RefreshMethodHandler implements MethodHandler {

    private final BeanDefinition beanDefinition;


    public RefreshMethodHandler(BeanDefinition beanDefinition) {
        this.beanDefinition = beanDefinition;
    }

    @Override
    public Object invoke(Object self, Method thismethod, Method proceed, Object[] args) throws Throwable {
        Object target=IocApplicationContext.getOrCreateRefreshTarget(beanDefinition);
        return thismethod.invoke(target, args);
    }

}
