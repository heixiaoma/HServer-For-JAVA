package top.test.service;

import top.hserver.core.ioc.annotation.Bean;

@Bean
@top.hserver.core.ioc.annotation.RpcService
public class RpcService {

    public String test(String name){
        System.out.println(666);
        return "我是RPC";
    }
}
