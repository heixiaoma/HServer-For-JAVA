package top.test.config;

import top.hserver.core.ioc.annotation.Bean;
import top.hserver.core.ioc.annotation.Configuration;
import top.test.bean.User;

@Configuration
public class DataConfig {

    @Bean("createUser")
    public User createUser(){
        User user = new User();
        user.setAge(999);
        user.setName("我是配置类自定义名字的数据");
        user.setSex("未知");
        return user;
    }


    @Bean
    public User createUser1(){
        User user = new User();
        user.setAge(999);
        user.setName("我是配置类的默认数据");
        user.setSex("未知");
        return user;
    }

}
