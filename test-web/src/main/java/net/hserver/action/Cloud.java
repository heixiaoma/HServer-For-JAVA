package net.hserver.action;

import net.hserver.service.RpcService;
import top.hserver.core.ioc.annotation.Controller;
import top.hserver.core.ioc.annotation.GET;
import top.hserver.core.ioc.annotation.Resource;

@Controller
public class Cloud {

  @Resource("Rpc")
  private RpcService rpcService;

  @GET("/rpc")
  public String rpc() {
    String test = "";
    long l = System.currentTimeMillis();
    for (int i = 0; i < 10; i++) {
      test = rpcService.test("7777");
    }
    long l1 = System.currentTimeMillis();
    return test+"---->10次调用耗时"+((l1-l)/1000.0);
  }
}
