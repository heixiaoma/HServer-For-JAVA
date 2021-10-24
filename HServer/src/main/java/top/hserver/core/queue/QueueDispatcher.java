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
import top.hserver.core.server.util.NamedThreadFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static top.hserver.core.server.context.ConstConfig.PERSIST_PATH;


/**
 * @author hxm
 */
public class QueueDispatcher {
    private static final Logger log = LoggerFactory.getLogger(QueueDispatcher.class);
    private static Map<String, QueueHandleInfo> handleMethodMap = new ConcurrentHashMap<>();
    private static Map<String, FQueue> FQ = new ConcurrentHashMap<>();

    private QueueDispatcher() {
    }

    public static void removeQueue(String queueName) {
        handleMethodMap.remove(queueName);
        FQueue fQueue = FQ.get(queueName);
        fQueue.clear();
        FQ.remove(queueName);
    }

    public static List<String> getAllQueueName() {
        return new ArrayList<>(FQ.keySet());
    }

    public static void addQueueListener(String queueName, Class clazz) {
        Object obj = IocUtil.getBean(clazz);
        if (obj == null) {
            log.error("{} 容器中不存在", clazz.getName());
            return;
        }
        QueueListener queueListener = obj.getClass().getAnnotation(QueueListener.class);
        if (queueListener == null) {
            log.error("{} 它不是一个消息监听器", clazz.getName());
            return;
        }
        IocUtil.addBean(queueName, obj);
        QueueHandleInfo eventHandleInfo = new QueueHandleInfo();
        eventHandleInfo.setQueueHandlerType(queueListener.type());
        eventHandleInfo.setQueueName(queueName);
        eventHandleInfo.setBufferSize(queueListener.bufferSize());
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            QueueHandler queueHandler = method.getAnnotation(QueueHandler.class);
            if (queueHandler != null) {
                eventHandleInfo.add(new QueueHandleMethod(method, queueHandler.size(), queueHandler.level()));
                log.debug("寻找队列 [{}] 的方法 [{}.{}]", queueName, clazz.getSimpleName(),
                        method.getName());
            }
        }
        handleMethodMap.put(queueName, eventHandleInfo);
        initConfigQueue(eventHandleInfo);
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
            Object obj;
            try {
                obj = clazz.newInstance();
            } catch (Exception e) {
                log.error("initialize " + clazz.getSimpleName() + " error", e);
                continue;
            }
            if (queueListener.queueName().trim().length() == 0) {
                IocUtil.addBean(obj);
                continue;
            }
            IocUtil.addBean(queueListener.queueName(), obj);
            QueueHandleInfo eventHandleInfo = new QueueHandleInfo();
            eventHandleInfo.setQueueHandlerType(queueListener.type());
            eventHandleInfo.setQueueName(queueListener.queueName());
            eventHandleInfo.setBufferSize(queueListener.bufferSize());
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

    private static void initConfigQueue(QueueHandleInfo v) {
        try {
            FQueue fQueue = new FQueue(PERSIST_PATH + File.separator + v.getQueueName());
            FQ.put(v.getQueueName(), fQueue);
        } catch (Exception ignored) {
        }
        QueueFactory queueFactory = new QueueFactoryImpl();
        queueFactory.createQueue(v.getQueueName(), v.getBufferSize(), v.getQueueHandlerType(), v.getQueueHandleMethods());
        v.setQueueFactory(queueFactory);
    }

    /**
     * 创建队列
     */
    public static void startTaskThread() {
        /**
         * 检查历史是否有，有的话先关闭掉
         */
        FQ.forEach((k, v) -> {
            try {
                v.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        FQ.clear();
        handleMethodMap.forEach((k, v) -> {
            initConfigQueue(v);
        });
        if (FQ.size() > 0) {
            Thread thread = new NamedThreadFactory("hserver_queue").newThread(() -> {
                while (true) {
                    FQ.forEach((k, v) -> {
                        try {
                            QueueInfo queueInfo = queueInfo(k);
                            if (queueInfo!=null&&queueInfo.getRemainQueueSize()>0) {
                                byte[] poll = v.poll();
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
                }
            });
            thread.start();
        }
    }

    /**
     * 分发队列
     *
     * @param queueName 事件URI
     * @param args      事件参数
     */
    public static boolean dispatcherQueue(String queueName, Object... args) {
        QueueHandleInfo queueHandleInfo = handleMethodMap.get(queueName);
        if (queueHandleInfo != null) {
            queueHandleInfo.getQueueFactory().producer(new QueueData(queueName, args));
            return true;
        } else {
            log.error("不存在:{} 队列", queueName);
            return false;
        }
    }


    /**
     * 持久化存储
     *
     * @param queueName
     * @param args
     */
    public static boolean dispatcherSerializationQueue(String queueName, Object... args) {
        FQueue fQueue = FQ.get(queueName);
        if (fQueue == null) {
            log.error("不存在:{} 队列", queueName);
            return false;
        }
        fQueue.offer(SerializationUtil.serialize(new QueueData(queueName, args)));
        return true;
    }

    public static QueueInfo queueInfo(String queueName) {
        QueueHandleInfo queueHandleInfo = handleMethodMap.get(queueName);
        if (queueHandleInfo != null) {
            QueueInfo queueInfo = queueHandleInfo.getQueueFactory().queueInfo();
            return queueInfo;
        }
        return null;
    }
}