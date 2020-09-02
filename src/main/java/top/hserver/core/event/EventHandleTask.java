package top.hserver.core.event;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import top.hserver.core.ioc.IocUtil;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

/**
 * 事件处理任务
 * @author hxm
 */
@Slf4j
@Data
public class EventHandleTask implements Runnable, Serializable {

    private Map<String, Object> eventParams;
    private String methodName;
    private String uri;
    private String className;
    private Class<?>[] parameterTypes;
    /**
     * 事件处理任务
     *
     * @param eventParams  事件参数
     */
    public EventHandleTask(String className,Map<String, Object> eventParams,String methodName,Class<?>[] parameterTypes) {
        this.eventParams = eventParams;
        if (this.eventParams == null) {
            this.eventParams = Collections.emptyMap();
        }
        this.methodName=methodName;
        this.parameterTypes=parameterTypes;
        this.className=className;
    }


    @Override
    public void run() {
        log.debug("运行eventHandleTask，事件URI: {}, 事件参数: {}", uri, eventParams);
        long t0 = System.currentTimeMillis();
        try {
            // 调用事件处理方法
            Class cl=ClassLoader.getSystemClassLoader().loadClass(className);
            Method method= cl.getMethod(methodName,parameterTypes);
            method.invoke(IocUtil.getBean(className), eventParams);
            log.debug("运行 eventHandleTask 成功, 耗时 {}ms", System.currentTimeMillis() - t0);
        } catch (Exception e) {
            if (e instanceof InvocationTargetException) {
                e = (Exception) e.getCause();
            }
            log.error("运行 eventHandleTask 错误", e);
        }
    }
}
