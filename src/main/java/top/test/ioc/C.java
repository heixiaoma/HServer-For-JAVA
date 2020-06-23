package top.test.ioc;

import top.hserver.core.ioc.annotation.Autowired;
import top.test.bean.User;

public class C {

  @Autowired
  private User user;

  public User getUser() {
    return user;
  }
}
