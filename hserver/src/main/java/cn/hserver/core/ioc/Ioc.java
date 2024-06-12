package cn.hserver.core.ioc;

import java.util.List;
import java.util.Map;

public interface Ioc {

    Map<String, Object> getAll();

    void addListBean(Object bean);

    <T> List<T> getListBean(Class<T> type);

    List<Object> getListBean(String beanName);

    void addListBean(String name, Object bean);

    void addBean(Object bean);

    void addBean(String name, Object bean);

    void remove(Class<?> type);

    void remove(String beanName);

    <T> T getBean(Class<T> type);

    <T> T getSupperBean(Class<T> type);

    <T> List<T> getSupperBeanList(Class<T> type);

    Object getBean(String beanName);

    <T> T getBean(String beanName, Class<T> type);

    void clearAll();

    boolean exist(String beanName);

    boolean exist(Class<?> type);

}
