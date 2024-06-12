package cn.hserver.core.ioc;

import java.util.List;
import java.util.Map;

/**
 * 简易IOC处理工具类
 *
 * @author hxm
 */
public class IocUtil {

    private final static Ioc IOC = new HServerIoc();

    public static Map<String, Object> getAll() {
        return IOC.getAll();
    }

    public static void addListBean(Object bean) {
        IOC.addListBean(bean);
    }

    public static <T> List<T> getListBean(Class<T> type) {
        return IOC.getListBean(type);
    }

    public static List<Object> getListBean(String beanName) {
        return IOC.getListBean(beanName);
    }

    public static void addListBean(String name, Object bean) {
        IOC.addListBean(name, bean);
    }

    public static <T> T getBean(Class<T> type) {
        return IOC.getBean(type);
    }

    public static <T> T getSupperBean(Class<T> type) {
        return IOC.getSupperBean(type);
    }
    public static <T> List<T> getSupperBeanList(Class<T> type) {
        return IOC.getSupperBeanList(type);
    }

    public static Object getBean(String beanName) {
        return IOC.getBean(beanName);
    }

    public static <T> T getBean(String beanName, Class<T> type) {
        return IOC.getBean(beanName, type);
    }

    public static void addBean(Object bean) {
        IOC.addBean(bean);
    }

    public static void addBean(String name, Object bean) {
        IOC.addBean(name, bean);
    }

    public static void remove(Class<?> type) {
        IOC.remove(type);
    }

    public static void remove(String beanName) {
        IOC.remove(beanName);
    }

    public static void clearAll() {
        IOC.clearAll();
    }

    public static boolean exist(String beanName) {
        return IOC.exist(beanName);
    }

    public static boolean exist(Class<?> type) {
        return IOC.exist(type);
    }

}
