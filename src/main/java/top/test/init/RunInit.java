package top.test.init;

import lombok.extern.slf4j.Slf4j;
import top.hserver.core.interfaces.InitRunner;
import top.hserver.core.ioc.annotation.Autowired;
import top.hserver.core.ioc.annotation.Bean;
import top.hserver.core.ioc.annotation.Value;
import top.test.bean.User;

@Slf4j
@Bean
public class RunInit implements InitRunner {

  @Value("statisticalRules")
  private String statisticalRules;

  @Autowired
  private User user;

  @Override
  public void init(String[] args) {
    System.out.println(statisticalRules);
    log.debug("初始化方法：注入的User对象的名字是-->{}", user.getName());
  }
}
