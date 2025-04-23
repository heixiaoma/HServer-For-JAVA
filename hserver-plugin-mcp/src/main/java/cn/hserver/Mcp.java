package cn.hserver;

import cn.hserver.core.server.util.JsonResult;
import cn.hserver.handler.McpHandler;
import cn.hserver.msg.ReqMsg;
import cn.hserver.plugin.web.annotation.Controller;
import cn.hserver.plugin.web.annotation.GET;
import cn.hserver.plugin.web.annotation.POST;
import cn.hserver.plugin.web.context.sse.SSeEvent;
import cn.hserver.plugin.web.context.sse.SSeStream;
import cn.hserver.plugin.web.interfaces.HttpRequest;
import cn.hserver.plugin.web.interfaces.HttpResponse;

import java.util.HashMap;
import java.util.Map;

@Controller
public class Mcp {

  public static final  Map<String, McpHandler> sseStreams = new HashMap<>();

    /**
     * @throws Exception
     */
    @POST("/message")
    public void message(ReqMsg reqMsg, HttpRequest request) throws Exception {
        System.out.println(reqMsg.getMethod());
        String query = request.query("sessionId");
        McpHandler mcpHandler = sseStreams.get(query);
        if (mcpHandler != null) {
            mcpHandler.handle(reqMsg);
        }
    }


    @GET("/sse")
    public void res(HttpRequest request, HttpResponse response) throws Exception {
        String s = request.getRequestId();
        SSeStream sSeStream = response.getSSeStream().addCloseListener(()->{
            sseStreams.remove(s);
            System.out.println("关闭："+s+"-->"+sseStreams.size());
        });
        System.out.println("Sess:"+s);
        sSeStream.sendSseEvent(new SSeEvent.Builder().event("endpoint").data("/message?sessionId="+s).build());
        sseStreams.put(s, new McpHandler(sSeStream));
    }

    @GET("/")
    public JsonResult jsonResult() {
        return JsonResult.ok();
    }

    @POST("/")
    public JsonResult postResult() {
        return JsonResult.ok();
    }

}
