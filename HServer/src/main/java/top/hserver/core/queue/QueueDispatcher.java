package top.hserver.core.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hserver.cloud.util.SerializationUtil;
import top.hserver.core.ioc.IocUtil;
import top.hserver.core.ioc.annotation.queue.QueueHandler;
import top.hserver.core.ioc.annotation.queue.QueueListener;
import top.hserver.core.ioc.ref.PackageScanner;
import top.hserver.core.queue.fqueue.FQueue;
import top.hserver.core.queue.fqueue.exception.FileFormatException;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static top.hserver.core.server.context.ConstConfig.PERSIST_PATH;


/**
 * @author hxm
 */
public class QueueDispatcher {

    private static final Logger log = LoggerFactory.getLogger(QueueDispatcher.class);

    private static Map<String, QueueHandleInfo> handleMethodMap = new ConcurrentHashMap<>();

    private static FQueue fQueue;

    static {
        try {
            fQueue = new FQueue(PERSIST_PATH);
        } catch (IOException | FileFormatException e) {
            e.printStackTrace();
        }
    }

    private QueueDispatcher() {
    }

    /**
     * 初始化事件分发器
     */
    public static void init(PackageScanner scanner) throws IOException {
        // 载入事件处理类
        List<Class<?>> classes = scanner.getAnnotationList(QueueListener.class);
        // 解析事件处理类
        for (Class<?> clazz : classes) {
            QueueListener queueListener = clazz.getAnnotation(QueueListener.class);
            if (queueListener == null) {
                continue;
            }
            QueueHandleInfo eventHandleInfo = new QueueHandleInfo();
            eventHandleInfo.setQueueHandlerType(queueListener.type());
            eventHandleInfo.setQueueName(queueListener.queueName());
            eventHandleInfo.setBufferSize(queueListener.bufferSize());
            Object obj;
            try {
                obj = clazz.newInstance();
            } catch (Exception e) {
                log.error("initialize " + clazz.getSimpleName() + " error", e);
                continue;
            }
            IocUtil.addBean(queueListener.queueName(), obj);
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                QueueHandler queueHandler = method.getAnnotation(QueueHandler.class);
                if (queueHandler != null) {
                    eventHandleInfo.add(new QueueHandleMethod(method, queueHandler.size(), queueHandler.level()));
                    log.debug("寻找队列 [{}] 的方法 [{}.{}]", queueListener.queueName(), clazz.getSimpleName(),
                            method.getName());
                }
            }
            handleMethodMap.put(queueListener.queueName(), eventHandleInfo);
        }

    }

    /**
     * 创建队列
     */
    public static void startTaskThread() {
        handleMethodMap.forEach((k, v) -> {
            QueueFactory queueFactory = new QueueFactoryImpl();
            queueFactory.createQueue(v.getQueueName(), v.getBufferSize(), v.getQueueHandlerType(), v.getQueueHandleMethods());
            v.setQueueFactory(queueFactory);
        });

        Thread thread = new Thread(() -> {
            try {
                while (true) {
                    byte[] poll = fQueue.poll();
                    if (poll == null) {
                        Thread.sleep(1000);
                    } else {
                        QueueData deserialize = SerializationUtil.deserialize(poll, QueueData.class);
                        dispatcherQueue(deserialize.getQueueName(), deserialize.getArgs());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.setName("SerializationQueue");
        thread.start();

    }

    /**
     * 分发队列
     *
     * @param queueName 事件URI
     * @param args      事件参数
     */
    public static void dispatcherQueue(String queueName, Object... args) {
        QueueHandleInfo queueHandleInfo = handleMethodMap.get(queueName);
        if (queueHandleInfo != null) {
            queueHandleInfo.getQueueFactory().producer(new QueueData(queueName, args));
        } else {
            log.error("不存在:{} 队列", queueName);
        }
    }


    /**
     * 持久化存储
     *
     * @param queueName
     * @param args
     */
    public static void dispatcherSerializationQueue(String queueName, Object... args) {
        fQueue.offer(SerializationUtil.serialize(new QueueData(queueName, args)));
    }

    public static QueueInfo queueInfo(String queueName) {
        QueueHandleInfo queueHandleInfo = handleMethodMap.get(queueName);
        if (queueHandleInfo != null) {
            QueueInfo queueInfo = queueHandleInfo.getQueueFactory().queueInfo();
            queueInfo.setFqueue(fQueue.size());
            return queueInfo;
        }
        return null;
    }
}