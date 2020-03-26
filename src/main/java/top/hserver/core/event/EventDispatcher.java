package top.hserver.core.event;

import lombok.extern.slf4j.Slf4j;
import top.hserver.core.ioc.IocUtil;
import top.hserver.core.ioc.annotation.event.Event;
import top.hserver.core.ioc.annotation.event.EventHandler;
import top.hserver.core.ioc.ref.PackageScanner;
import top.hserver.core.server.epoll.NamedThreadFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 事件分发器<br>
 * <br>
 * 在代码中初始化（只需初始化一次），参数为事件处理器类的包名：<br>
 */
@Slf4j
public class EventDispatcher {
  private static Map<String, EventHandleMethod> handleMethodMap = new ConcurrentHashMap<>();
  private static ExecutorService handlePool = null;
  public static final LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();

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
          handleMethodMap.put(eventUri, new EventHandleMethod(clazz.getName(),method, eventUri));
          log.debug("寻找事件 [{}] 的方法 [{}.{}]", eventUri, clazz.getSimpleName(),
            method.getName());
        }
      }
    }
    // 初始化事件处理线程池
    int nThreads = Runtime.getRuntime().availableProcessors();
    handlePool = new ThreadPoolExecutor(nThreads, nThreads * 2, 0L, TimeUnit.MILLISECONDS, queue, new NamedThreadFactory("hserver_ queue@"));
  }

  /**
   * 分发事件
   *
   * @param eventUri    事件URI
   * @param eventParams 事件参数
   */
  protected static void dispartchEvent(String eventUri, Map<String, Object> eventParams) {
    EventHandleMethod handleMethod = handleMethodMap.get(eventUri);
    if (handleMethod == null) {
      log.warn("无法通过eventUri找到eventHandleMethod: {}", eventUri);
      return;
    }
    handlePool.execute(new EventHandleTask(handleMethod, eventParams));
  }
}