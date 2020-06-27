package top.hserver.core.interfaces;

/**
 * Aop
 * @author hxm
 */
public interface HookAdapter {

    /**
     * 之前
     * @param args
     */
    void before(Object[] args);

    /**
     * 之后
     * @param object
     * @return
     */
    Object after(Object object);
}
