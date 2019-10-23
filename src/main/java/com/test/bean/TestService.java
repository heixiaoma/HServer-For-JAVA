package com.test.bean;

import com.hserver.core.ioc.annotation.Bean;

@Bean
public class TestService {

    public String test() {
        return "牛皮得很";
    }

}
