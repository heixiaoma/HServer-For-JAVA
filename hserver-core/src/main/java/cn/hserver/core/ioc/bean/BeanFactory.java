package cn.hserver.core.ioc.bean;

public interface BeanFactory {
    Object getBean(String name) throws Exception;
    <T> T getBean(Class<T> requiredType) throws Exception;
    boolean containsBean(String name);
}    