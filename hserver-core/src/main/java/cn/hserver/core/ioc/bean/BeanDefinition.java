package cn.hserver.core.ioc.bean;

import java.lang.reflect.Constructor;
import java.util.List;

public class BeanDefinition {
    private String beanName;
    private Class<?> beanClass;
    private String scope = "singleton";
    private Constructor<?> constructor;
    private List<PropertyValue> propertyValues;

    // getters and setters
    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

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

    public List<PropertyValue> getPropertyValues() {
        return propertyValues;
    }

    public void setPropertyValues(List<PropertyValue> propertyValues) {
        this.propertyValues = propertyValues;
    }
}    