package cn.hserver.plugin.web.interfaces;


import cn.hserver.plugin.web.handlers.Ws;

/**
 * @author hxm
 */
public interface WebSocketHandler {

    /**
     * 连接
     *
     * @param ws
     */
    void onConnect(Ws ws);

    /**
     * 来消息
     *
     * @param ws
     */
    void onMessage(Ws ws);

    /**
     * 断开
     *
     * @param ws
     */
    void disConnect(Ws ws);

    default String getSubProtocols() {

        return null;
    }

}
