package top.hserver.core.interfaces;

import top.hserver.core.server.handlers.Ws;

public interface WebSocketHandler {

    void onConnect(Ws ws);

    void onMessage(Ws ws);

    void disConnect(Ws ws);

}
