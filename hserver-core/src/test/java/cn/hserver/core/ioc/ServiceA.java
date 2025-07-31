package cn.hserver.core.ioc;

import cn.hserver.core.boot.HServerApplication;
import cn.hserver.core.config.annotation.Value;
import cn.hserver.core.ioc.annotation.Component;
import cn.hserver.core.ioc.annotation.Qualifier;
import cn.hserver.core.scheduling.annotation.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.List;

@Component
public class ServiceA {
    private static final Logger log = LoggerFactory.getLogger(ServiceA.class);

    private final Service serviceB;

    private final Service serviceC;

    @Value("hserver")
    private List<LinkedHashMap> data;

    private final ConfigTest configTest;

    public ServiceA( Service serviceB,@Qualifier("serviceB") Service serviceC,ConfigTest configTest) {
        this.serviceB = serviceB;
        this.serviceC = serviceC;
        this.configTest = configTest;
    }

    public void doSomething() {
        serviceB.sayHello();
        serviceC.sayHello();
    }

    @Task(name = "AA", time ="*/5 * * * * ?")
    private void a(String a){
        log.info("aaa"+System.currentTimeMillis()+data);
        doSomething();
        System.out.println(configTest.getAge());
        System.out.println(configTest.getHeight());
        System.out.println(configTest.getName());
        HServerApplication.stop();
    }

}
