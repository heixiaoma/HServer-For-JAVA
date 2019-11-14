package com.test.service;

import com.hserver.core.ioc.annotation.Autowired;
import com.hserver.core.ioc.annotation.Bean;

@Bean
public class Test {

    @Autowired()
    private TestService testService;

    public String show() {
        return testService.testa();
    }

    public String ac(){
        return "牛皮得很";
    }
}
