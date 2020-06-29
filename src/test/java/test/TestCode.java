package test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import top.hserver.core.ioc.annotation.Autowired;
import top.hserver.core.test.HServerTest;
import top.test.bean.User;

@RunWith(HServerTest.class)
public class TestCode {

  @Autowired
  private User user;

  @Autowired
  private ApiService apiService;

  @Before
  public void before() {
    System.out.println();
    apiService.sayHello();
  }

  @After
  public void After() {
    System.out.println(user.getName());
  }

  @Test
  public void test(){
    apiService.sayHello();
  }





}
