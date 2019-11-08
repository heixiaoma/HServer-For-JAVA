package com.test.bean;

import com.hserver.core.ioc.annotation.Bean;
import com.hserver.core.ioc.annotation.In;

@Bean
public class TestService {

    @In
    private Test test;

    public String testa() {
        return test.ac();
    }

}
