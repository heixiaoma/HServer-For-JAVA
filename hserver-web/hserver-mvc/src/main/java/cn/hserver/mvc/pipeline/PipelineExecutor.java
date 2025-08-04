package cn.hserver.mvc.pipeline;

import cn.hserver.mvc.context.WebContext;
import cn.hserver.mvc.server.WebServer;

public class PipelineExecutor {

    public static void executor(WebContext webContext) {
         WebServer.router.matchAndHandle(webContext);
    }
}
