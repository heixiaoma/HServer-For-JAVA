package cn.hserver.core.ioc.bean;

@FunctionalInterface
public interface ObjectFactory<T> {
    /**
     * 获取对象实例
     * @return 对象实例
     */
    T getObject();
}