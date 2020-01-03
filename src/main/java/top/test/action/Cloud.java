package top.test.action;

import top.hserver.core.ioc.annotation.Controller;
import top.hserver.core.ioc.annotation.GET;
import top.hserver.core.ioc.annotation.Resource;
import top.test.service.RpcService;

@Controller
public class Cloud {

    @Resource("Rpc")
    private RpcService rpcService;

    @GET("/rpc")
    public String rpc(){
        String test = rpcService.test("666");
        return test;
    }
}
