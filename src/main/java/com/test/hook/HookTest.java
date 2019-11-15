package com.test.hook;

import com.hserver.core.ioc.HookAdapter;
import com.hserver.core.ioc.annotation.Hook;
import com.test.service.Test;

@Hook(value = Test.class, method = "show")
public class HookTest implements HookAdapter {

    @Override
    public void before(Object[] objects) {
        System.out.println("------before-----");
    }

    @Override
    public Object after(Object object) {
        System.out.println("------after-----");
        return object;
    }
}
