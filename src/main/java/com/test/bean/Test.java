package com.test.bean;

import com.hserver.core.ioc.annotation.Bean;
import com.hserver.core.ioc.annotation.In;

@Bean
public class Test {

    @In
    private TestService testService;

    public void show() {
        testService.test();
    }
}
