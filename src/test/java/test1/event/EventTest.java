package test1.event;

import test1.service.HelloService;
import top.hserver.core.ioc.annotation.Autowired;
import top.hserver.core.ioc.annotation.event.Event;
import top.hserver.core.ioc.annotation.event.EventHandler;

import java.util.Map;

@EventHandler("/aa/aa")
public class EventTest {

  @Autowired
  private HelloService helloService;

  @Event("aa")
  public void aa(Map params) {
    try {
      System.out.println(Thread.currentThread().getName() + helloService.sayHello());
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Event("bb")
  public void bb(Map params) {
    try {
      System.out.println(Thread.currentThread().getName());
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
