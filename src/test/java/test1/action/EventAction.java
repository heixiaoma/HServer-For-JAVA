package test1.action;

import top.hserver.core.event.HServerEvent;
import top.hserver.core.ioc.annotation.Controller;
import top.hserver.core.ioc.annotation.GET;
import top.hserver.core.server.util.JsonResult;

import java.util.HashMap;
import java.util.Map;

@Controller
public class EventAction {

    @GET("/event")
    public JsonResult event() {
        for (int i = 0; i < 10000000; i++) {
            HServerEvent.sendEvent("Queue", "666");
        }
        return JsonResult.ok();
    }

}
