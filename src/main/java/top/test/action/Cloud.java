package top.test.action;

import top.hserver.core.ioc.annotation.Controller;
import top.hserver.core.ioc.annotation.GET;
import top.hserver.core.ioc.annotation.Resource;
import top.test.service.RpcService;

@Controller
public class Cloud {

    @Resource
    private RpcService rpcService;

    @GET("/prc")
    public String rpc(){
        rpcService.test("666");
        return "0";
    }
}
