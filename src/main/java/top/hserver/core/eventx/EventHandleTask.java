package top.hserver.core.eventx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Map;

/**
 * 事件处理任务
 */
public class EventHandleTask implements Runnable, Comparable<EventHandleTask> {
    private static final Logger LOG = LoggerFactory.getLogger(EventHandleTask.class);

    private EventHandleMethod handleMethod;
    private int priority;
    private long createTime;
    private Map<String, Object> eventParams;

    /**
     * 事件处理任务
     *
     * @param handleMethod 事件处理方法
     * @param eventParams  事件参数
     */
    public EventHandleTask(EventHandleMethod handleMethod, Map<String, Object> eventParams) {
        this.handleMethod = handleMethod;
        this.priority = handleMethod.getPriority();
        this.createTime = System.currentTimeMillis();
        this.eventParams = eventParams;
        if (this.eventParams == null) {
            this.eventParams = Collections.emptyMap();
        }
    }

    public void run() {
        LOG.debug("运行eventHandleTask，事件URI: {}, 事件参数: {}", handleMethod.getUri(), eventParams);
        long t0 = System.currentTimeMillis();

        try {

            // 调用事件处理方法
            handleMethod.getMethod().invoke(handleMethod.getHandler(), eventParams);

            LOG.debug("运行 eventHandleTask 成功, 耗时 {}ms", System.currentTimeMillis() - t0);
        } catch (Exception e) {
            if (e instanceof InvocationTargetException) {
                e = (Exception) e.getCause();
            }
            LOG.error("运行 eventHandleTask 错误", e);
        }
    }

    public int compareTo(EventHandleTask other) {
        // 优先级高的排前
        if (this.priority > other.priority) {
            return -1;
        } else if (this.priority < other.priority) {
            return 1;
        }
        // 创建时间早的排前
        if (this.createTime < other.createTime) {
            return -1;
        } else if (this.createTime > other.createTime) {
            return 1;
        }
        return 0;
    }

    public void increPriority() {
        priority++;
    }

    @Override
    public String toString() {
        return String.format("{优先:%d,uri:%s,参数:%s}", priority, handleMethod.getUri(), eventParams.toString());
    }
}
