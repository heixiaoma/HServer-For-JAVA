package top.hserver.core.interfaces;

import top.hserver.core.server.handlers.Ws;
import top.hserver.core.server.handlers.Wsc;

/**
 * @author hxm
 */
public interface WebSocketClientHandler {

    /**
     * 连接
     * @param wsc
     */
    void onConnect(Wsc wsc);

    /**
     * 来消息
     * @param wsc
     */
    void onMessage(Wsc wsc);

    /**
     * 断开
     * @param wsc
     */
    void disConnect(Wsc wsc);

    /**
     * pong
     * @param wsc
     */
    void pong(Wsc wsc);

    /**
     * 异常
     * @param e
     */
    void throwable(Wsc wsc,Throwable e);

}
