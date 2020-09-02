package top.hserver.core.event;

import lombok.extern.slf4j.Slf4j;
import top.hserver.core.event.queue.QueueFactory;
import top.hserver.core.event.queue.QueueFactoryImpl;
import top.hserver.core.ioc.IocUtil;
import top.hserver.core.ioc.annotation.event.Event;
import top.hserver.core.ioc.annotation.event.EventHandler;
import top.hserver.core.ioc.ref.PackageScanner;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;


/**
 *
 * @author hxm
 */
@Slf4j
public class EventDispatcher {
    private static Map<String, EventHandleInfo> handleMethodMap = new ConcurrentHashMap<>();

    private EventDispatcher() {
    }

    /**
     * 初始化事件分发器
     */
    public static void init(PackageScanner scanner) throws IOException {
        // 载入事件处理类
        List<Class<?>> classes = scanner.getAnnotationList(EventHandler.class);
        // 解析事件处理类
        for (Class<?> clazz : classes) {
            EventHandler eventHandler = clazz.getAnnotation(EventHandler.class);
            if (eventHandler == null) {
                continue;
            }
            EventHandleInfo eventHandleInfo = new EventHandleInfo();
            eventHandleInfo.setEventHandlerType(eventHandler.type());
            eventHandleInfo.setQueueName(eventHandler.queueName());
            eventHandleInfo.setBufferSize(eventHandler.bufferSize());
            Object obj;
            try {
                obj = clazz.newInstance();
            } catch (Exception e) {
                log.error("initialize " + clazz.getSimpleName() + " error", e);
                continue;
            }
            IocUtil.addBean(eventHandler.queueName(), obj);
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                Event eventMethod = method.getAnnotation(Event.class);
                if (eventMethod != null) {
                    eventHandleInfo.add(new EventHandleMethod(method,eventMethod.size(), eventMethod.level()));
                    log.debug("寻找队列 [{}] 的方法 [{}.{}]", eventHandler.queueName(), clazz.getSimpleName(),
                            method.getName());
                }
            }
            handleMethodMap.put(eventHandler.queueName(), eventHandleInfo);
        }
    }

    /**
     * 创建队列
     */
    public static void startTaskThread() {
        handleMethodMap.forEach((k, v) -> {
            QueueFactory queueFactory = new QueueFactoryImpl();
            queueFactory.createQueue(v.getQueueName(), v.getBufferSize(), v.getEventHandlerType(), v.getEventHandleMethods());
            v.setQueueFactory(queueFactory);

        });
    }

    /**
     * 分发事件
     *
     * @param queueName 事件URI
     * @param args      事件参数
     */
    public static void dispatcherEvent(String queueName, Object... args) {
        EventHandleInfo eventHandleInfo = handleMethodMap.get(queueName);
        if (eventHandleInfo != null) {
            eventHandleInfo.getQueueFactory().producer(args);
        } else {
            log.error("不存在:{} 队列", queueName);
        }
    }

}