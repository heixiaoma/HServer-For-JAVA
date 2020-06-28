package top.hserver.core.event.queue;

import top.hserver.core.server.util.NamedThreadFactory;

import java.util.HashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SpongeThreadPoolExecutor {
  public final static String FilePersistence_Dir = "directory";
  public final static String BlockingQueue_Capacity = "capacity";
  public final static String BlockingQueue_OnePersistLimit = "onePersistLimit";
  public final static String MaxByteArray_Sz = "maxByteArray_Sz";
  public final static String OneBatchWriteCnt = "oneBatchWriteCnt";
  public final static String CanReleaseResMaxTime = "canReleaseResMaxTime";

  public static SpongeArrayBlockingQueue tmpMyArrayBlockingQueue;

  public SpongeThreadPoolExecutor() {

  }

  /**
   * 创建一个默认以文件为持久化缓冲的线程池
   *
   * @param corePoolSize    同java自带ThreadPoolExecutor初始化参数
   * @param maximumPoolSize 同java自带ThreadPoolExecutor初始化参数
   * @param keepAliveTime   同java自带ThreadPoolExecutor初始化参数
   * @param timeUnit        同java自带ThreadPoolExecutor初始化参数
   * @param parmHMap        key-value方式的参数:
   *                        1.FilePersistence_Dir: 持久化文件目录,如 d:/testThread、/root/netcomm;
   *                        2.BlockingQueue_Capacity：存放在内存中的任务数量,默认500;
   *                        3.BlockingQueue_OnePersistLimit：一次执行批量持久化的任务数上限,默认100;
   *                        4.MaxByteArray_Sz：最大允许的内存数,单位byte,默认50 * 1024 * 1024(50M)
   *                        5.OneBatchWriteCnt：进行一次持久化从内存队列中一批最多可以处理的个数,默认20
   *                        6.CanReleaseResMaxTime：如果连续等待这么长时间还没有任何持久化的读、写操作，
   *                        则删除相关资源，如删除序列化的文件,默认60s。
   * @return
   * @throws SpongeException
   */
  public static ThreadPoolExecutor generateThreadPoolExecutor(
    int corePoolSize, int maximumPoolSize, long keepAliveTime,
    TimeUnit timeUnit, HashMap parmHMap)
    throws SpongeException {
    String tmpDirectory = null;
    int tmpCapacity = 1000;
    int tmpOnePersistLimit = 100;
    long tmpMaxByteArray_Sz = 50 * 1024 * 1024;
    int tmpOneBatchWriteCnt = 20;
    int tmpCanReleaseResMaxTime = 60 * 1000;

    ThreadPoolExecutor tmpThreadPool = null;
    try {
      if (parmHMap != null) {
        tmpDirectory = (String) parmHMap.get(FilePersistence_Dir);
        if (tmpDirectory == null) {
          throw new SpongeException("parmHMap里缺少 " + FilePersistence_Dir + " 设置");
        }

        String tmpCapStr = (String) parmHMap.get(BlockingQueue_Capacity);
        if (tmpCapStr != null) {
          tmpCapacity = Integer.parseInt(tmpCapStr);
        }

        String tmpLimit = (String) parmHMap.get(BlockingQueue_OnePersistLimit);
        if (tmpLimit != null) {
          tmpOnePersistLimit = Integer.parseInt(tmpLimit);
        }

        String tmpMaxByteArray_SzStr = (String) parmHMap.get(MaxByteArray_Sz);
        if (tmpMaxByteArray_SzStr != null) {
          tmpMaxByteArray_Sz = Long.parseLong(tmpMaxByteArray_SzStr);
        }

        String tmpOneBatchWriteCntStr = (String) parmHMap.get(OneBatchWriteCnt);
        if (tmpOneBatchWriteCntStr != null) {
          tmpOneBatchWriteCnt = Integer.parseInt(tmpOneBatchWriteCntStr);
        }

        String tmpCanReleaseResMaxTimeStr = (String) parmHMap.get(CanReleaseResMaxTime);
        if (tmpCanReleaseResMaxTimeStr != null) {
          tmpCanReleaseResMaxTime = Integer.parseInt(tmpCanReleaseResMaxTimeStr);
        }
      } else {
        throw new SpongeException("parmHMap 不能为null");
      }

      FilePersistence tmpFilePersistence = new FilePersistence(tmpMaxByteArray_Sz,
        tmpOneBatchWriteCnt, tmpCanReleaseResMaxTime, tmpDirectory);
      SpongeService tmpSpongeService = new SpongeService(tmpFilePersistence);
      tmpMyArrayBlockingQueue =
        new SpongeArrayBlockingQueue(tmpCapacity, tmpOnePersistLimit, tmpSpongeService);
      tmpThreadPool =
        new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, timeUnit,
          tmpMyArrayBlockingQueue, new NamedThreadFactory("hserver_ queue@"));
      tmpMyArrayBlockingQueue.doFetchData_init(tmpThreadPool);
    } catch (Throwable ex) {
      ex.printStackTrace();
      throw new SpongeException(ex.getLocalizedMessage());
    }

    return tmpThreadPool;
  }

  public static ThreadPoolExecutor generateThreadPoolExecutor(
    int corePoolSize, int maximumPoolSize, long keepAliveTime,
    TimeUnit timeUnit, HashMap parmHMap,
    PersistenceIntf thePersistenceInsParm)
    throws SpongeException {
    int tmpCapacity = 500;
    int tmpOnePersistLimit = 100;

    ThreadPoolExecutor tmpThreadPool = null;
    try {
      if (parmHMap != null) {
        String tmpCapStr = (String) parmHMap.get(BlockingQueue_Capacity);
        if (tmpCapStr != null) {
          tmpCapacity = Integer.parseInt(tmpCapStr);
        }

        String tmpLimit = (String) parmHMap.get(BlockingQueue_OnePersistLimit);
        if (tmpLimit != null) {
          tmpOnePersistLimit = Integer.parseInt(tmpLimit);
        }
      }

      if (thePersistenceInsParm == null) {
        throw new SpongeException("持久化插件不能为null");
      }

      SpongeService tmpSpongeService = new SpongeService(thePersistenceInsParm);
      SpongeArrayBlockingQueue tmpMyArrayBlockingQueue =
        new SpongeArrayBlockingQueue(tmpCapacity, tmpOnePersistLimit, tmpSpongeService);
      tmpThreadPool =
        new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, timeUnit,
          tmpMyArrayBlockingQueue);
      tmpMyArrayBlockingQueue.doFetchData_init(tmpThreadPool);
    } catch (Throwable ex) {
      ex.printStackTrace();
      throw new SpongeException(ex.getLocalizedMessage());
    }

    return tmpThreadPool;
  }
}
