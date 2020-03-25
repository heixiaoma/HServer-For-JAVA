package top.test.action;

import top.hserver.core.event.HServerEvent;
import top.hserver.core.ioc.annotation.Controller;
import top.hserver.core.ioc.annotation.GET;
import top.hserver.core.server.util.JsonResult;

import java.util.HashMap;
import java.util.Map;

@Controller
public class EventAction {

    @GET("/event")
    public JsonResult event(){
        Map params = new HashMap();
        params.put("a", "aaaaaaaaaa");
        params.put("b", 1234);
        params.put("c", 0);
        params.put("d", true);
        HServerEvent.sendEvent("/aa/aa/aa", params);
        return JsonResult.ok();
    }

    @GET("/queueSize")
    public JsonResult getQueueSize(){
        int size = HServerEvent.queueSize();
        return JsonResult.ok().put("size",size);
    }

}
