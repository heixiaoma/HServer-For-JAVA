package net.hserver.core.interfaces;

import net.hserver.core.server.context.HumMessage;
import net.hserver.core.server.handlers.Hum;

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
