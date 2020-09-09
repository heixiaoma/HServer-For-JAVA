package test1.service;

import test1.log.Log;
import top.hserver.core.ioc.annotation.Bean;

@Bean
public class XXXXImp implements HelloService {
    @Override
    public String sayHello() {
        return "实现类";
    }
}
