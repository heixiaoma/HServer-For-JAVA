package test1;
import org.junit.Test;
import org.junit.runner.RunWith;
import top.hserver.core.test.HServerTestServer;

/**
 * @author hxm
 */
@RunWith(HServerTestServer.class)
public class TestWebApp {

  @Test
  public void start(){
    System.out.println("运行我就可以了.");
  }

}
