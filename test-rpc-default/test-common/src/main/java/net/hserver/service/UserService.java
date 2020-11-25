package net.hserver.service;

import java.util.concurrent.CompletableFuture;

public interface UserService {

    String getUserInfo();

    void setUserInfo(String userInfo);

}
