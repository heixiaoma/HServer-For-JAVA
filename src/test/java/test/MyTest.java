package test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import top.hserver.core.ioc.annotation.Autowired;
import top.hserver.core.ioc.annotation.HServerBootTest;
import top.hserver.core.test.HServerTest;
import top.test.TestWebApp;
import top.test.annotation.Aa;
import top.test.bean.User;

@RunWith(HServerTest.class)
@HServerBootTest(TestWebApp.class)
public class MyTest {

  @Test
  public void test() {
    new Aa().loga("ok");
  }


}
