package top.test.service;

import top.hserver.core.ioc.annotation.Autowired;
import top.hserver.core.ioc.annotation.Bean;
import top.hserver.core.ioc.annotation.Track;

@Bean
public class Test {

    @Autowired
    private TestService testService;

    public String show(String name) {
        return testService.testa() + name;
    }

    public String ac() {
        return "牛皮得很";
    }
}
