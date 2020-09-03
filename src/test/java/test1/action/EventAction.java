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
        long start = System.currentTimeMillis();
        int j = 100000000;
        for (int i = 0; i < j; i++) {
            HServerQueue.sendQueue("Queue", "666");
        }
        return JsonResult.ok().put(j + "个队列，耗时：", System.currentTimeMillis() - start + "ms");
    }


    @GET("/eventInfo")
    public JsonResult eventInfo() {
        QueueInfo queueInfo = HServerQueue.queueInfo("Queue");
        return JsonResult.ok().put("data", queueInfo);
    }

}
