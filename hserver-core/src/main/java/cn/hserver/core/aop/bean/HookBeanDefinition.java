package cn.hserver.core.aop.bean;

import cn.hserver.core.aop.HookAdapter;
import cn.hserver.core.context.IocApplicationContext;

import java.lang.reflect.Method;

public class HookBeanDefinition {

    private final Class<?> hookHandler;

    private final Method[] hookMethod;

    private HookAdapter hookAdapter;

    public Class<?> getHookHandler() {
        return hookHandler;
    }

    public Method[] getHookMethod() {
        return hookMethod;
    }

    public HookBeanDefinition(Class<?> hookHandler, Method[] hookMethod) {
        this.hookHandler = hookHandler;
        this.hookMethod = hookMethod;
    }


    public boolean isMatchMethod(Method method){
        for (Method hookMethod : this.hookMethod) {
            if (hookMethod.equals(method)){
                return true;
            }
        }
        return false;
    }

    public HookAdapter getHookAdapter() {
        if (hookAdapter == null){
            hookAdapter= (HookAdapter)IocApplicationContext.getBean(hookHandler);
        }
        return hookAdapter;
    }

}
