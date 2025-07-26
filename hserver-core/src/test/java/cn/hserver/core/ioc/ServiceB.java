package cn.hserver.core.ioc;

import cn.hserver.core.ioc.annotation.Autowired;
import cn.hserver.core.ioc.annotation.Component;
import cn.hserver.core.ioc.annotation.Configuration;
import cn.hserver.core.ioc.annotation.PostConstruct;

import javax.sql.DataSource;

@Component
public class ServiceB {

    @Autowired
    private  ServiceA serviceA;

    @Autowired
    private UserService userService;

    @Autowired
    private DataSource dataSource;

    public String doSomething() {
        System.out.println(serviceA);
        System.out.println(dataSource);
        System.out.println(userService.getDataSource());
        return "ServiceB is working";
    }

    @PostConstruct
    public void  a(){
        System.out.println("ServiceB======"+serviceA);
    }
}    