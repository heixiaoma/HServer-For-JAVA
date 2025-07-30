package cn.hserver.core.ioc;

import cn.hserver.core.ioc.annotation.Autowired;
import cn.hserver.core.ioc.annotation.Component;
import cn.hserver.core.ioc.annotation.Qualifier;
import cn.hserver.core.scheduling.annotation.Task;

@Component
public class ServiceA {
    private final Service serviceB;

    private final Service serviceC;

    public ServiceA( Service serviceB,@Qualifier("serviceB") Service serviceC) {
        this.serviceB = serviceB;
        this.serviceC = serviceC;
    }

    public void doSomething() {
        serviceB.sayHello();
        serviceC.sayHello();
    }

    @Task(name = "AA", time ="*/5 * * * * ?")
    private void a(String a){
        System.out.println(a);
        doSomething();
    }

}
