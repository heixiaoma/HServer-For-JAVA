package com.hserver.core.interfaces;

import com.hserver.core.server.handlers.Ws;
import io.netty.channel.ChannelHandlerContext;

public interface WebSocketHandler {

    void onConnect(Ws ws);

    void onMessage(Ws ws);

    void disConnect(Ws ws);

}
