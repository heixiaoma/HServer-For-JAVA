package top.hserver.core.interfaces;

/**
 * Aop
 */
public interface HookAdapter {

    void before(Object[] args);

    Object after(Object object);
}