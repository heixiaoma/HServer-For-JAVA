package cn.hserver.core.ioc;

import cn.hserver.core.ioc.annotation.Component;

@Component
public class ServiceB {
    public String doSomething() {
        return "ServiceB is working";
    }
}    