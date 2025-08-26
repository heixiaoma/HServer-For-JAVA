package cn.hserver.mvc.server;


import cn.hserver.core.config.ConfigData;
import cn.hserver.mvc.router.Router;

public interface WebServer {

    Router router=new Router();

    static void webPort(int port) {
        ConfigData.getInstance().set("web.port", port);
    }


    void start(int port,int sslPort,SslData sslData);
    // 停止服务
    void stop();
}
