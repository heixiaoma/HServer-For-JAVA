package com.hserver.core.ioc;

import java.util.Map;

/**
 * 简易IOC处理工具类
 */
public class IocUtil {

    private final static Ioc ioc = new HServerIoc();

    public static Map<String, Object> getAll(){
        return ioc.getAll();
    }

    public static <T> T getBean(Class<T> type) {
        return ioc.getBean(type);
    }

    public static Object getBean(String beanName) {
        return ioc.getBean(beanName);
    }

    public static <T> T getBean(String beanName, Class<T> type) {
        return ioc.getBean(beanName, type);
    }

    public static void addBean(Object bean) {
        ioc.addBean(bean);
    }

    public static void addBean(String name, Object bean) {
        ioc.addBean(name, bean);
    }

    public static void remove(Class<?> type) {
        ioc.remove(type);
    }

    public static void remove(String beanName) {
        ioc.remove(beanName);
    }

    public static void clearAll() {
        ioc.clearAll();

    }

    public static boolean exist(String beanName) {
        return ioc.exist(beanName);
    }

    public static boolean exist(Class<?> type) {
        return ioc.exist(type);
    }

}
