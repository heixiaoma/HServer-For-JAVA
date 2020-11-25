package net.hserver.service;

import top.hserver.core.ioc.annotation.Bean;
import top.hserver.core.ioc.annotation.RpcService;

@Bean
@RpcService
public class UserServiceImpl implements UserService {

    public String getUserInfo() {

        return "我是用户信息";
    }

    public void setUserInfo(String userInfo) {
        System.out.println("设置了用户信息:" + userInfo);
    }
}
