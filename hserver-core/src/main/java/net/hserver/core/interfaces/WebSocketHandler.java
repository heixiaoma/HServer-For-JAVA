package net.hserver.core.interfaces;

import net.hserver.core.server.handlers.Ws;

/**
 * @author hxm
 */
public interface WebSocketHandler {

    /**
     * 连接
     * @param ws
     */
    void onConnect(Ws ws);

    /**
     * 来消息
     * @param ws
     */
    void onMessage(Ws ws);

    /**
     * 断开
     * @param ws
     */
    void disConnect(Ws ws);

}
