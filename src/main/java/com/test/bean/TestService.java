package com.test.bean;

import com.hserver.core.ioc.annotation.Autowired;
import com.hserver.core.ioc.annotation.Bean;

@Bean
public class TestService {

    @Autowired
    private Test test;

    public String testa() {
        return test.ac();
    }

}
