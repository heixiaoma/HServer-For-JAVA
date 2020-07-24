package test2;

import org.junit.Test;
import org.junit.runner.RunWith;
import top.hserver.core.ioc.annotation.Autowired;
import top.hserver.core.test.HServerTest;

@RunWith(HServerTest.class)
public class test2 {

  @Autowired
  private TestBean testBean;

  @Autowired
  private Tom tom;

  @Test
  public void test(){
    System.out.println(testBean.hello());
  }



  @Test
  public void test2(){
    System.out.println(tom.toString());
  }


}
