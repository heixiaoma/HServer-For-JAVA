package net.hserver.action;

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

    @GET("/event2")
    public JsonResult event2() {
        HServerQueue.sendPersistQueue("Queue", "666");
        return JsonResult.ok();
    }

    @GET("/event3")
    public JsonResult event3() {
        long l = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            HServerQueue.sendPersistQueue("Queue", "666---》" + i);
        }
        return JsonResult.ok("10w耗时：" + ((System.currentTimeMillis() - l) / 1000.0) + "s");
    }

}
