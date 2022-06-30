package cn.hserver.core.interfaces;

import cn.hserver.core.server.context.HumMessage;
import cn.hserver.core.server.handlers.Hum;

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
