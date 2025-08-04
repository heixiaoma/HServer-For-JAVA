package cn.hserver.mvc.server;


import cn.hserver.mvc.router.Router;

public interface WebServer {

    Router router=new Router();

    // 启动服务
    void start(int port);
    // 停止服务
    void stop();


}
