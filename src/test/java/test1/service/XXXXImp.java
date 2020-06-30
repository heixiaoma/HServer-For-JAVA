package test1.service;

import top.hserver.core.ioc.annotation.Bean;

@Bean
public class XXXXImp implements HelloService {
    @Override
    public String sayHello() {
        return "实现类";
    }
}
