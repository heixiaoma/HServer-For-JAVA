package net.hserver.action;

import top.hserver.core.interfaces.HttpRequest;
import top.hserver.core.ioc.annotation.Controller;
import top.hserver.core.ioc.annotation.GET;
import top.hserver.core.ioc.annotation.POST;
import top.hserver.core.server.util.JsonResult;

@Controller
public class TestAction {


    @GET("/testGet")
    public JsonResult get(HttpRequest request){
        return  JsonResult.ok();
    }

    @POST("/testPost")
    public JsonResult post(HttpRequest request){
        return  JsonResult.ok();
    }
}
