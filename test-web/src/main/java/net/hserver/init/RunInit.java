package net.hserver.init;

import net.hserver.bean.User;
import net.hserver.log.Log;
import top.hserver.core.interfaces.InitRunner;
import top.hserver.core.ioc.annotation.Autowired;
import top.hserver.core.ioc.annotation.Bean;
import top.hserver.core.ioc.annotation.Value;

@Bean
public class RunInit implements InitRunner {

  @Value("ENDPOINT")
  private String env;

  @Autowired
  private User user;

  @Log
  @Override
  public void init(String[] args) {
    System.out.println("初始化方法：注入的User对象的名字是-->{}"+ user.getName()+env);
  }
}
