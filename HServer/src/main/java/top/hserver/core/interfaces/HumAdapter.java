package top.hserver.core.interfaces;

import top.hserver.core.server.context.HumMessage;
import top.hserver.core.server.handlers.Hum;

/**
 * @author hxm
 */
public interface HumAdapter {
    /**
     * 消息回调
     *
     * @param data
     */
    void message(HumMessage data, Hum hum);
}
