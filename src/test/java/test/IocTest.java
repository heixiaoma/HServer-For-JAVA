package test;

import org.junit.Test;
import org.junit.runner.RunWith;
import top.hserver.core.ioc.annotation.Autowired;
import top.hserver.core.ioc.annotation.HServerBootTest;
import top.hserver.core.test.HServerTest;
import top.test.TestWebApp;
import top.test.ioc.A;

@RunWith(HServerTest.class)
@HServerBootTest(TestWebApp.class)
public class IocTest {

  @Autowired
  private A a;

  @Test
  public void test(){
    System.out.println(a.getUser().getName());
  }
}
