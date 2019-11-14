package com.test.hook;

import com.hserver.core.ioc.annotation.After;
import com.hserver.core.ioc.annotation.Before;
import com.hserver.core.ioc.annotation.Hook;
import com.hserver.core.ioc.annotation.Replace;
import com.test.service.Test;

@Hook
public class HookTest {

    @Before(Test.class)
    public void show(String name) {
        System.out.println("蛤蟆皮");
    }

    @Replace(Test.class)
    public void show(Integer a) {
        System.out.println("蛤蟆皮");
    }

    @After(Test.class)
    public void show(Boolean flag) {
        System.out.println("蛤蟆皮");
    }
}
