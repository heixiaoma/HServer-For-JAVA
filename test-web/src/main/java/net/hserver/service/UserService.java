package net.hserver.service;


import net.hserver.bean.User;
import top.hserver.core.ioc.annotation.Autowired;
import top.hserver.core.ioc.annotation.Bean;

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
