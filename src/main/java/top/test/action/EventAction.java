package top.test.action;

import top.hserver.core.eventx.EventX;
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
        EventX.sendEvent("/test/aa", params);
        return JsonResult.ok();
    }

}
