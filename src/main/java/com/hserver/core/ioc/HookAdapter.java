package com.hserver.core.ioc;

public interface HookAdapter {

    void before(Object[] args);

    Object after(Object object);
}
