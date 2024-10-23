package cn.hserver.plugin.web.handlers.check;

import cn.hserver.plugin.web.context.HServerContext;
import cn.hserver.plugin.web.context.HServerContextHolder;
import cn.hserver.plugin.web.handlers.StaticHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticFile implements DispatcherHandler{
    private static final Logger log = LoggerFactory.getLogger(StaticFile.class);
    private final static StaticHandler staticHandler = new StaticHandler();

    @Override
    public HServerContext dispatcher(HServerContext context) {
        if (context.getWebkit().httpResponse.hasData()) {
            return context;
        }
        if (staticHandler.hasEmptyStaticFile()){
            return context;
        }
        cn.hserver.plugin.web.context.StaticFile handler = staticHandler.handler(context.getRequest().getUri(), context);
        if (handler != null) {
            context.setStaticFile(true);
            context.setStaticFile(handler);
        }
        return context;
    }
}
