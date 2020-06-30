package test1.ioc;

import top.hserver.core.ioc.annotation.Autowired;
import test1.bean.User;

public class C {

  @Autowired
  private User user;

  public User getUser() {
    return user;
  }
}
