package cn.hserver.core.boot;

import cn.hserver.core.config.ConfigData;
import cn.hserver.core.context.IocApplicationContext;
import cn.hserver.core.logging.HServerLogAsyncAppender;
import cn.hserver.core.logging.HServerLogConfig;
import cn.hserver.core.logging.LogAdapter;
import cn.hserver.core.queue.QueueManager;
import cn.hserver.core.scheduling.TaskManager;
import cn.hserver.core.util.EnvironmentUtil;
import cn.hserver.core.util.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

public class HServerApplication {
    private static final Logger log = LoggerFactory.getLogger(HServerApplication.class);
    private static final CountDownLatch shutdownLatch = new CountDownLatch(1);
    private static boolean running = false;

    static {
        Thread shutdown = new NamedThreadFactory("hserver_shutdown").newThread(() -> {
            log.info("服务即将关闭");
//            List<ServerCloseAdapter> listBean = IocUtil.getListBean(ServerCloseAdapter.class);
            log.info("服务关闭完成");
        });
        Runtime.getRuntime().addShutdownHook(shutdown);
    }

    private static Class<?> mainClass;

    public static synchronized void run(Class<?> mainClass, String... args) {
        if (!running) {
            running = true;
            HServerApplication.mainClass = mainClass;
            //初始化环境
            EnvironmentUtil.init(mainClass);
            //初始化配置
            ConfigData.getInstance();
            //启动log配置
            HServerLogConfig.init();
            //启动IOC容器
            new IocApplicationContext(packages());
            //启动队列
            QueueManager.startQueueServer();
            //启动定时任务
            TaskManager.startTask();
            //启动完成
            success();
            try {
                shutdownLatch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }else {
            log.info("HServer已启动");
        }
    }

    public static void stop() {
        if (running) {
            shutdownLatch.countDown();
        }
    }

    private static Set<String> packages(){
        Set<String>   scanPackage = new HashSet<>();
        if (mainClass != null) {
            scanPackage.add(mainClass.getPackage().getName());
        }
//        scanPackage.addAll(PlugsManager.getPlugin().getPlugPackages());
        scanPackage.add(HServerApplication.class.getPackage().getName());
        return scanPackage;
    }


    private static void success(){
        log.info("HServer启动成功");
        HServerLogAsyncAppender.setHasLog(IocApplicationContext.getBeansOfType(LogAdapter.class));
    }

}
