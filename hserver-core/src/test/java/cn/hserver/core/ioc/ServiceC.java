package cn.hserver.core.ioc;

import cn.hserver.core.aop.annotation.Hook;
import cn.hserver.core.ioc.annotation.Autowired;
import cn.hserver.core.ioc.annotation.Component;
import cn.hserver.core.ioc.annotation.PostConstruct;

import javax.sql.DataSource;

@Hook(value = ServiceA.class)
public class ServiceC {

    @Autowired
    private UserService userService;

    @PostConstruct
    public void  a(){
        System.out.println("PostConstruct+++++ServiceC======");
    }
}    