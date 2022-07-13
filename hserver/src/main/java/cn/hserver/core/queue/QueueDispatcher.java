package cn.hserver.core.queue;

import cn.hserver.core.queue.cache.CacheMap;
import cn.hserver.core.queue.cache.HQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.hserver.core.server.util.ExceptionUtil;
import cn.hserver.core.server.util.PropUtil;
import cn.hserver.core.ioc.IocUtil;
import cn.hserver.core.ioc.annotation.queue.QueueHandler;
import cn.hserver.core.ioc.annotation.queue.QueueListener;
import cn.hserver.core.ioc.ref.PackageScanner;
import cn.hserver.core.server.util.NamedThreadFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

import static cn.hserver.core.server.context.ConstConfig.PERSIST_PATH;


/**
 * @author hxm
 */
public class QueueDispatcher {
    private static final Logger log = LoggerFactory.getLogger(QueueDispatcher.class);
    private static final Map<String, QueueHandleInfo> handleMethodMap = new ConcurrentHashMap<>();
    private static final Map<String, HQueue> CACHE_MAP_MAP = new ConcurrentHashMap<>();
    private static final int buffSize = 1024;

    private QueueDispatcher() {
    }

    public static void removeQueue(String queueName, boolean trueDelete) {
        QueueHandleInfo queueHandleInfo = handleMethodMap.get(queueName);
        if (queueHandleInfo != null && queueHandleInfo.getQueueFactory() != null) {
            queueHandleInfo.getQueueFactory().stop();
        }
        handleMethodMap.remove(queueName);
        HQueue hQueue = CACHE_MAP_MAP.get(queueName);
        if (hQueue != null) {
            if (trueDelete) {
                hQueue.clear();
            }
            hQueue.close();
        }
        CACHE_MAP_MAP.remove(queueName);
    }

    public static List<String> getAllQueueName() {
        return new ArrayList<>(CACHE_MAP_MAP.keySet());
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
        eventHandleInfo.setBufferSize(buffSize);
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            QueueHandler queueHandler = method.getAnnotation(QueueHandler.class);
            if (queueHandler != null) {
                int size=queueHandler.size();
                String s = queueHandler.sizePropValue();
                if (s.trim().length()!=0){
                    Integer anInt = PropUtil.getInstance().getInt(s);
                    if (anInt!=null&&anInt>0) {
                        size =anInt;
                    }
                }
                eventHandleInfo.add(new QueueHandleMethod(method, queueHandler.level(), size));
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
        Set<Class<?>> classes = scanner.getAnnotationList(QueueListener.class);
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
            eventHandleInfo.setBufferSize(buffSize);
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                QueueHandler queueHandler = method.getAnnotation(QueueHandler.class);
                if (queueHandler != null) {
                    int size=queueHandler.size();
                    String s = queueHandler.sizePropValue();
                    if (s.trim().length()!=0){
                        Integer anInt = PropUtil.getInstance().getInt(s);
                        if (anInt!=null) {
                            size =anInt;
                        }
                    }
                    eventHandleInfo.add(new QueueHandleMethod(method, queueHandler.level(), size));
                    log.debug("寻找队列 [{}] 的方法 [{}.{}]", queueListener.queueName(), clazz.getSimpleName(),
                            method.getName());
                }
            }
            handleMethodMap.put(queueListener.queueName(), eventHandleInfo);
        }
    }

    private static void initConfigQueue(QueueHandleInfo v) {
        try {
            CACHE_MAP_MAP.put(v.getQueueName(), new HQueue(PERSIST_PATH + File.separator + v.getQueueName()));
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        QueueFactory queueFactory = new QueueFactoryImpl();
        queueFactory.createQueue(v.getQueueName(), v.getBufferSize(), v.getQueueHandlerType(), v.getQueueHandleMethods());
        v.setQueueFactory(queueFactory);
        v.getQueueFactory().start();
    }

    /**
     * 创建队列
     */
    public static void startTaskThread() {
        /**
         * 检查历史是否有，有的话先关闭掉
         */
        handleMethodMap.forEach((k, v) -> {
            if (v.getQueueFactory() != null) {
                v.getQueueFactory().stop();
            }
        });
        CACHE_MAP_MAP.forEach((k, v) -> {
            try {
                v.close();
            } catch (Exception e) {
                log.error(ExceptionUtil.getMessage(e));
            }
        });
        CACHE_MAP_MAP.clear();
        //再来重新开始
        handleMethodMap.forEach((k, v) -> {
            initConfigQueue(v);
        });
        Thread thread = new NamedThreadFactory("hserver_queue").newThread(() -> {
            while (true) {
                if (CACHE_MAP_MAP.size() > 0) {
                    CACHE_MAP_MAP.forEach((k, v) -> {
                        try {
                            QueueInfo queueInfo = queueInfo(k);
                            QueueHandleInfo queueHandleInfo = handleMethodMap.get(k);
                            if (queueHandleInfo == null) {
                                sleep();
                                return;
                            }
                            //剩余队列大于0个，就说明内存还能放
                            if (queueInfo != null && (queueInfo.getRemainQueueSize() >0)) {
                                QueueData first = v.getFirst();
                                if (first != null) {
                                    dispatcherQueue(first, first.getQueueName());
                                } else {
                                    sleep();
                                }
                            } else {
                                sleep();
                            }
                        } catch (Exception e) {
                            sleep();
                            e.printStackTrace();
                            log.error(ExceptionUtil.getMessage(e));
                        }
                    });
                } else {
                    sleep();
                }
            }
        });
        thread.start();
    }

    /**
     * 分发队列
     *
     * @param queueName 事件URI
     */
    private static boolean dispatcherQueue(QueueData queueData, String queueName) {
        QueueHandleInfo queueHandleInfo = handleMethodMap.get(queueName);
        if (queueHandleInfo != null) {
            if (queueData != null) {
                queueData.sethQueue(CACHE_MAP_MAP.get(queueName));
                queueHandleInfo.getQueueFactory().producer(queueData);
            }
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
        HQueue hQueue = CACHE_MAP_MAP.get(queueName);
        if (hQueue == null) {
            log.error("不存在:{} 队列", queueName);
            return false;
        }
        QueueData queueData = new QueueData(queueName, args);
        hQueue.put(queueData);
        return true;
    }

    public static QueueInfo queueInfo(String queueName) {
        QueueHandleInfo queueHandleInfo = handleMethodMap.get(queueName);
        if (queueHandleInfo != null && queueHandleInfo.getQueueFactory() != null) {
            QueueInfo queueInfo = queueHandleInfo.getQueueFactory().queueInfo();
            queueInfo.setFqueueSize(CACHE_MAP_MAP.get(queueName).size());
            return queueInfo;
        }
        return null;
    }


    private static void sleep(){
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            log.error(ExceptionUtil.getMessage(e));
        }
    }

}
