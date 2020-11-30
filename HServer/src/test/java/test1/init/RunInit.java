package test1.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test1.bean.User;
import test1.log.Log;
import top.hserver.core.interfaces.InitRunner;
import top.hserver.core.ioc.annotation.Autowired;
import top.hserver.core.ioc.annotation.Bean;
import top.hserver.core.ioc.annotation.Value;

@Bean
public class RunInit implements InitRunner {
  private static final Logger log = LoggerFactory.getLogger(RunInit.class);
  @Value("ENDPOINT")
  private String env;

  @Autowired
  private User user;

  @Log
  @Override
  public void init(String[] args) {
    log.debug("初始化方法：注入的User对象的名字是-->{}", user.getName()+env);
  }
}
