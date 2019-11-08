package com.test.bean;

import com.hserver.core.ioc.annotation.Bean;
import com.hserver.core.ioc.annotation.In;

@Bean
public class Test {

    @In
    private TestService testService;

    public String show() {
        return testService.testa();
    }

    public String ac(){
        return "牛皮得很";
    }
}
