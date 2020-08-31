package top.hserver.core.event;

import lombok.extern.slf4j.Slf4j;
import top.hserver.core.event.queue.SpongeThreadPoolExecutor;
import top.hserver.core.ioc.IocUtil;
import top.hserver.core.ioc.annotation.event.Event;
import top.hserver.core.ioc.annotation.event.EventHandler;
import top.hserver.core.ioc.ref.PackageScanner;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

import static top.hserver.core.server.context.ConstConfig.queuePool;

/**
 * 事件分发器<br>
 * <br>
 * 在代码中初始化（只需初始化一次），参数为事件处理器类的包名：<br>
 *
 * @author hxm
 */
@Slf4j
public class EventDispatcher {
    private static Map<String, EventHandleMethod> handleMethodMap = new ConcurrentHashMap<String, EventHandleMethod>();
    private static ExecutorService handlePool = null;

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
            EventHandler handlerAnno = clazz.getAnnotation(EventHandler.class);
            if (handlerAnno == null) {
                continue;
            }
            String module = handlerAnno.value();
            Object obj = null;
            try {
                obj = clazz.newInstance();
            } catch (Exception e) {
                log.error("initialize " + clazz.getSimpleName() + " error", e);
                continue;
            }
            IocUtil.addBean(clazz.getName(), obj);
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                Event eventAnno = method.getAnnotation(Event.class);
                if (eventAnno != null) {
                    String eventName = eventAnno.value();
                    String eventUri = null;
                    if (eventName.startsWith("/")) {
                        eventUri = eventName;
                    } else {
                        eventUri = module + "/" + eventName;
                    }
                    handleMethodMap.put(eventUri, new EventHandleMethod(clazz.getName(), method, eventUri));
                    log.debug("寻找事件 [{}] 的方法 [{}.{}]", eventUri, clazz.getSimpleName(),
                            method.getName());
                }
            }
        }
    }


    public static void startTaskThread() {
        if (handleMethodMap.size() > 0) {
            try {
                String path = System.getProperty("user.dir") + File.separator + "queue";
                String flag = "../";
                if (path.contains(flag)) {
                    return;
                }
                File file = new File(path);
                if (!file.exists()) {
                    file.mkdirs();
                }
                HashMap tmpParmHMap = new HashMap();
                tmpParmHMap.put(SpongeThreadPoolExecutor.FilePersistence_Dir, path);
                handlePool = SpongeThreadPoolExecutor.generateThreadPoolExecutor(
                        queuePool, queuePool * 2, 60L, TimeUnit.SECONDS, tmpParmHMap);
            } catch (Exception e) {
            }
        }
    }

    public static void clearFile() {
        String path = System.getProperty("user.dir") + File.separator + "queue";
        String flag = "../";
        if (path.contains(flag)) {
            return;
        }
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 分发事件
     *
     * @param eventUri    事件URI
     * @param eventParams 事件参数
     */
    protected static void dispartchEvent(String eventUri, Map<String, Object> eventParams) {
        EventHandleMethod task = handleMethodMap.get(eventUri);
        if (task == null) {
            log.error("不存在,{},url映射", eventUri);
            Set<String> strings = handleMethodMap.keySet();
            log.warn("当前存在的 URL， {}", strings);
            return;
        }
        handlePool.execute(new EventHandleTask(task.getClassName(), eventParams, task.getMethod().getName(), task.getParameterTypes()));
    }
}