package cn.hserver.core.ioc;

import cn.hserver.core.ioc.annotation.Component;

@Component("c")
public class ServiceC implements Service{
    @Override
    public void sayHello() {
        System.out.println("hello ServiceC");
    }
}
