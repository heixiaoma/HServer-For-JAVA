package cn.hserver.core.aop.bean;

import java.lang.reflect.Method;

public class HookBeanDefinition {

    private final Class<?> hookHandler;

    private final Method[] hookMethod;


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
}
