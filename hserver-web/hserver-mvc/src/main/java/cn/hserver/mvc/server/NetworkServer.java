package cn.hserver.mvc.server;


public interface NetworkServer {
    // 启动服务
    void start(int port);
    // 停止服务
    void stop();
}
