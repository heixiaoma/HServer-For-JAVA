package net.hserver.ioc;

import net.hserver.bean.User;
import top.hserver.core.ioc.annotation.Autowired;

public class C {

  @Autowired
  private User user;

  public User getUser() {
    return user;
  }
}
