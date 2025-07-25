package cn.hserver.core.ioc;

import cn.hserver.core.ioc.annotation.Autowired;
import cn.hserver.core.ioc.annotation.Component;

@Component
public class ServiceA {

    private  final ServiceB serviceB;

    public ServiceA(ServiceB serviceB) {
        this.serviceB = serviceB;
    }


    public String doSomething() {
        return "ServiceA: " + serviceB.doSomething();
    }
}    