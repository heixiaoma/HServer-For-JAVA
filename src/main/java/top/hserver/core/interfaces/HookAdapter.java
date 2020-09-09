package top.hserver.core.interfaces;

import java.lang.reflect.Method;

/**
 * Aop
 * @author hxm
 */
public interface HookAdapter {

    /**
     * 之前
     * @param args
     */
    void before(Class clazz, Method method, Object[] args);

    /**
     * 之后
     * @param object
     * @return
     */
    Object after(Class clazz, Method method, Object object);
}
