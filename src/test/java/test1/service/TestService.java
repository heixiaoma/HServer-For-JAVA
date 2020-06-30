package test1.service;

import top.hserver.core.ioc.annotation.Autowired;
import top.hserver.core.ioc.annotation.Bean;

@Bean
public class TestService {
    @Autowired
    private Test test;

    public String testa() {
        return test.ac();
    }

}
