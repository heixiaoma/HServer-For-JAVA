package top.test.service;

import top.hserver.core.ioc.annotation.Autowired;
import top.hserver.core.ioc.annotation.Bean;
import top.test.bean.User;

@Bean
public class UserService {

    @Autowired("createUser")
    private User user1;

    @Autowired
    private User user2;


    public User getUser1() {
        return user1;
    }


    public User getUser2() {
        return user2;
    }


}
