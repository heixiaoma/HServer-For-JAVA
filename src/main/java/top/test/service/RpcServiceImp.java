package top.test.service;

import top.hserver.core.ioc.annotation.Bean;
import top.hserver.core.ioc.annotation.RpcService;

@Bean
@RpcService("Rpc")
public class RpcServiceImp implements top.test.service.RpcService {
    @Override
    public String test(String name) {
        return "RpC"+name;
    }
}
