package test1.action;

import top.hserver.core.ioc.annotation.Controller;
import top.hserver.core.ioc.annotation.GET;
import top.hserver.core.queue.HServerQueue;
import top.hserver.core.queue.QueueInfo;
import top.hserver.core.server.util.JsonResult;


@Controller
public class EventAction {

    @GET("/event")
    public JsonResult event() {
        HServerQueue.sendQueue("Queue", "666");
        return JsonResult.ok();
    }


    @GET("/eventInfo")
    public JsonResult eventInfo() {
        QueueInfo queueInfo = HServerQueue.queueInfo("Queue");
        return JsonResult.ok().put("data", queueInfo);
    }

}
