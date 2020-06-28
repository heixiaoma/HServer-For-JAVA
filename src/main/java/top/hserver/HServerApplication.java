package top.hserver;

import top.hserver.cloud.CloudManager;
import top.hserver.core.ioc.ref.InitBean;
import top.hserver.core.ioc.ref.MemoryInitClass;
import top.hserver.core.log.HServerLogConfig;
import top.hserver.core.properties.PropertiesInit;
import top.hserver.core.server.HServer;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static top.hserver.core.event.EventDispatcher.startTaskThread;


/**
 * @author hxm
 */
@Slf4j
public class HServerApplication {

  public static void run(Class classz, Integer port, String... args) {
    iocInit(classz);
    startServer(port, args);
  }

  public static void run(Integer port, String... args) {
    iocInit();
    startServer(port, args);
  }

  private static void startServer(int port, String[] args) {
    //云启动
    CloudManager.run();
    try {
      new HServer(port, args).run();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void iocInit(Class... classz) {
    /**
     * 初始化哈日志配置
     */
    new HServerLogConfig().init();
    log.info("初始化配置文件");
    PropertiesInit.init();
    log.info("初始化配置完成");
    log.info("Class动态修改开始...");
    for (Class aClass : classz) {
      MemoryInitClass.init(aClass);
    }
    MemoryInitClass.init(HServerApplication.class);
    log.info("Class动态修改完成");
    log.info("HServer 启动中....");
    log.info("Package 扫描中");
    InitBean.init(HServerApplication.class);
    for (Class aClass : classz) {
      InitBean.init(aClass);
    }
    log.info("IOC 装配中");
    InitBean.injection();
    //Beetlsql注入
    InitBean.BeetlSqlinit();
    log.info("IOC 全部装配完成");
  }

  public static void runTest(List<Class> list) {
    iocInit(list.toArray(new Class[list.size()]));
    //Event Task 问题
    startTaskThread();
  }

}
