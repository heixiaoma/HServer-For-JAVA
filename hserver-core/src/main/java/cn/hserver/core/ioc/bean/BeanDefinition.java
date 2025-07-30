package cn.hserver.core.ioc.bean;

import cn.hserver.core.aop.bean.HookBeanDefinition;
import cn.hserver.core.ioc.annotation.ScopeType;
import cn.hserver.core.scheduling.bean.TaskDefinition;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class BeanDefinition {
    private Class<?> beanClass;
    private ScopeType scope = ScopeType.SINGLETON;
    private Constructor<?> constructor;
    private String factoryBeanName;
    private Method factoryMethod;
    private HookBeanDefinition hookBeanDefinition;

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public ScopeType getScope() {
        return scope;
    }

    public void setScope(ScopeType scope) {
        this.scope = scope;
    }

    public boolean isSingleton() {
        return ScopeType.SINGLETON == scope;
    }

    public boolean isPrototype() {
        return ScopeType.PROTOTYPE == scope;
    }

    public Constructor<?> getConstructor() {
        return constructor;
    }

    public void setConstructor(Constructor<?> constructor) {
        this.constructor = constructor;
    }

    public String getFactoryBeanName() {
        return factoryBeanName;
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }

    public Method getFactoryMethod() {
        return factoryMethod;
    }

    public void setFactoryMethod(Method factoryMethod) {
        this.factoryMethod = factoryMethod;
    }

    public HookBeanDefinition getHookBeanDefinition() {
        return hookBeanDefinition;
    }

    public void setHookBeanDefinition(HookBeanDefinition hookBeanDefinition) {
        this.hookBeanDefinition = hookBeanDefinition;
    }

    public boolean isHook() {
        return hookBeanDefinition != null;
    }
}