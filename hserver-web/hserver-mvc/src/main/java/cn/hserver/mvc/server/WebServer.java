package cn.hserver.mvc.server;


import cn.hserver.mvc.router.Router;

public interface WebServer {

    Router router=new Router();

    void start(int port,int sslPort,SslData sslData);
    // 停止服务
    void stop();
}
