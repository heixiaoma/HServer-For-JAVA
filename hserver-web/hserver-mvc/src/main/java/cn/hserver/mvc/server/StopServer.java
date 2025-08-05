package cn.hserver.mvc.server;

import cn.hserver.core.ioc.annotation.Component;
import cn.hserver.core.life.CloseAdapter;
import cn.hserver.mvc.MvcPlugin;

@Component
public class StopServer implements CloseAdapter {
    @Override
    public void close() {
        if (MvcPlugin.webServer != null) {
            MvcPlugin.webServer.stop();
        }
    }
}
