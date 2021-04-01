package net.hserver.action;

import net.hserver.log.Log;
import top.hserver.core.ioc.annotation.Controller;
import top.hserver.core.ioc.annotation.GET;
import top.hserver.core.server.util.JsonResult;

/**
 * @author hxm
 */
@Controller("/hook")
public class HookAction {

    @Log
    @GET("/get1")
    public JsonResult get1(){
        return JsonResult.ok();
    }

    @GET("/get2")
    public JsonResult get2(){
        return JsonResult.ok();
    }
}
