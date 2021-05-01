package net.hserver.websocketclient;

import top.hserver.core.interfaces.WebSocketClientHandler;
import top.hserver.core.ioc.annotation.WebSocketClient;
import top.hserver.core.server.handlers.Wsc;

/**
 * @author hxm
 */
//@WebSocketClient(url = "ws://123.207.136.134:9010/ajaxchattest")
public class WebSocketClientTest1 implements WebSocketClientHandler {

    @Override
    public void onConnect(Wsc wsc) {
        System.out.println("-----2连接了");
        wsc.send("{\"sub\":\"market.overview\"}");
    }

    @Override
    public void onMessage(Wsc wsc) {
        System.out.println("-----2来消息了：" + wsc.getText());
        wsc.send(String.valueOf(System.currentTimeMillis()));
    }

    @Override
    public void disConnect(Wsc wsc) {
        System.out.println("-----2断开了");
    }

    @Override
    public void pong(Wsc wsc) {
        System.out.println("-----2来心跳了");
    }

    @Override
    public void throwable(Wsc wsc, Throwable e) {
        System.out.println("-----2异常了");
    }
}
