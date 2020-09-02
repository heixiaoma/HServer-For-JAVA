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
        long start = System.currentTimeMillis();
        int j=100000000;
        for (int i = 0; i < j; i++) {
            HServerEvent.sendEvent("Queue", "666");
        }
        return JsonResult.ok().put(j+"个队列，耗时：",System.currentTimeMillis()-start+"ms");
    }

}
