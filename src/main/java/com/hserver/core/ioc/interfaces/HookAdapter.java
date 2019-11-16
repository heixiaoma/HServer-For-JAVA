package com.hserver.core.ioc.interfaces;

/**
 * Aop
 */
public interface HookAdapter {

    void before(Object[] args);

    Object after(Object object);
}
