package test;

import top.hserver.core.ioc.annotation.Bean;

@Bean
public class ApiService {
    public void sayHello(){
        System.out.println("哈密皮");
    }
}
