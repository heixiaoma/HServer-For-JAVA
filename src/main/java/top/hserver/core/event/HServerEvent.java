package top.hserver.core.event;

import java.util.Map;

/**
 * 异步事件处理入口类
 */
public class HServerEvent{

    /**
     * 发送事件
     *
     * @param eventUri    事件URI，格式：/模块名/事件名
     * @param eventParams 事件参数
     */
    public static void sendEvent(String eventUri, Map<String, Object> eventParams) {
        EventDispatcher.dispartchEvent(eventUri, eventParams);
    }

    public static int queueSize(){
        return EventDispatcher.queue.size();
    }
}
