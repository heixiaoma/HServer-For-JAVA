package cn.hserver.core.ioc.bean;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

public class BeanDefinition {
    private Class<?> beanClass;
    private String scope = "singleton";
    private Constructor<?> constructor;
    private String factoryBeanName;
    private Method factoryMethod;

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public boolean isSingleton() {
        return "singleton".equals(scope);
    }

    public boolean isPrototype() {
        return "prototype".equals(scope);
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
}