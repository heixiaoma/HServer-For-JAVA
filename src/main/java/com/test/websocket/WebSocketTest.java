package com.test.websocket;

import com.hserver.core.interfaces.WebSocketHandler;
import com.hserver.core.ioc.annotation.Autowired;
import com.hserver.core.ioc.annotation.WebSocket;
import com.hserver.core.server.handlers.Ws;
import com.test.service.TestService;

@WebSocket("/ws")
public class WebSocketTest implements WebSocketHandler {

    @Autowired
    private TestService testService;

    @Override
    public void onConnect(Ws ws) {
        System.out.println("连接成功,分配的UID：" + ws.getUid());
    }

    @Override
    public void onMessage(Ws ws) {
        ws.send("666" + testService.testa() + ws.getUid());
        System.out.println("收到的消息,"+ws.getMessage()+",UID：" + ws.getUid());
    }

    @Override
    public void disConnect(Ws ws) {
        System.out.println("断开连接,UID:" + ws.getUid());
    }
}
