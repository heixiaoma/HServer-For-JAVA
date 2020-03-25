package top.hserver.core.event;

import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Map;

/**
 * 事件处理任务
 */
@Slf4j
public class EventHandleTask implements Runnable{
    private EventHandleMethod handleMethod;
    private Map<String, Object> eventParams;

    /**
     * 事件处理任务
     *
     * @param handleMethod 事件处理方法
     * @param eventParams  事件参数
     */
    public EventHandleTask(EventHandleMethod handleMethod, Map<String, Object> eventParams) {
        this.handleMethod = handleMethod;
        this.eventParams = eventParams;
        if (this.eventParams == null) {
            this.eventParams = Collections.emptyMap();
        }
    }


    @Override
    public void run() {
        log.debug("运行eventHandleTask，事件URI: {}, 事件参数: {}", handleMethod.getUri(), eventParams);
        long t0 = System.currentTimeMillis();
        try {
            // 调用事件处理方法
            handleMethod.getMethod().invoke(handleMethod.getHandler(), eventParams);
            log.debug("运行 eventHandleTask 成功, 耗时 {}ms", System.currentTimeMillis() - t0);
        } catch (Exception e) {
            if (e instanceof InvocationTargetException) {
                e = (Exception) e.getCause();
            }
            log.error("运行 eventHandleTask 错误", e);
        }
    }
}
