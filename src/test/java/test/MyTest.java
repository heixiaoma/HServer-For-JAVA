package test;

import org.junit.Test;
import org.junit.runner.RunWith;
import top.hserver.core.test.HServerTest;
import top.test.annotation.Aa;

@RunWith(HServerTest.class)
public class MyTest {


  @Test
  public void test() {
    new Aa().loga("ok");
  }


}
