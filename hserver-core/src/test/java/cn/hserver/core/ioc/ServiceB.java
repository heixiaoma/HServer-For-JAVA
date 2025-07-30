package cn.hserver.core.ioc;

import cn.hserver.core.ioc.annotation.Component;

@Component

public class ServiceB implements Service{
    @Override
    public void sayHello() {
        System.out.println("hello ServiceB");
    }
}
