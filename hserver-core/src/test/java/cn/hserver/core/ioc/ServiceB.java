package cn.hserver.core.ioc;

import cn.hserver.core.ioc.annotation.Autowired;
import cn.hserver.core.ioc.annotation.Component;

@Component
public class ServiceB {

    @Autowired
    private  ServiceA serviceA;

    public String doSomething() {
        System.out.println(serviceA);
        return "ServiceB is working";
    }
}    