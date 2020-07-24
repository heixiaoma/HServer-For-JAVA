package test2;

import top.hserver.core.ioc.annotation.Autowired;
import top.hserver.core.ioc.annotation.Bean;
import top.hserver.core.ioc.annotation.Configuration;

@Configuration
public class Config {

    @Autowired
    private Tom tom;

    @Bean
    private void tom(){
        System.out.println(tom);
    }

}
