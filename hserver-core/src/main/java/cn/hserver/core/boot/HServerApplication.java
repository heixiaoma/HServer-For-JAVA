package cn.hserver.core.boot;

import cn.hserver.core.config.ConfigData;
import cn.hserver.core.context.IocApplicationContext;
import cn.hserver.core.life.CloseAdapter;
import cn.hserver.core.life.InitAdapter;
import cn.hserver.core.life.StartAdapter;
import cn.hserver.core.logging.HServerLogAsyncAppender;
import cn.hserver.core.logging.HServerLogConfig;
import cn.hserver.core.logging.LogAdapter;
import cn.hserver.core.plugin.PluginManager;
import cn.hserver.core.queue.QueueManager;
import cn.hserver.core.scheduling.TaskManager;
import cn.hserver.core.util.EnvironmentUtil;
import cn.hserver.core.util.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

public class HServerApplication {
    private static final Logger log = LoggerFactory.getLogger(HServerApplication.class);
    private static final CountDownLatch shutdownLatch = new CountDownLatch(1);
    private static boolean running = false;

    static {
        Thread shutdown = new NamedThreadFactory("hserver_shutdown").newThread(() -> {
            log.info("服务即将关闭");
            List<CloseAdapter> beansOfType = IocApplicationContext.getBeansOfType(CloseAdapter.class);
            for (CloseAdapter closeAdapter : beansOfType) {
                closeAdapter.close();
            }
            log.info("服务关闭完成");
        });
        Runtime.getRuntime().addShutdownHook(shutdown);
    }

    private static Class<?> mainClass;


    public static synchronized void run(Class<?> mainClass, String... args){
        runCore(mainClass,null,null, args);
    }

    public static synchronized void runTest(Class<?> testClass,String testPackageName){
        runCore(null,testClass, testPackageName);
    }


    private static synchronized void runCore(Class<?> mainClass,Class<?> testClass,String testPackageName, String... args) {
        if (!running) {
            running = true;
            HServerApplication.mainClass = mainClass;
            //插件启动
            PluginManager.getPlugin().startApp();
            //初始化配置
            ConfigData.getInstance();
            //初始化环境
            EnvironmentUtil.init(testClass);
            //启动log配置
            HServerLogConfig.init();
            PluginManager.getPlugin().ioc();
            //启动IOC容器
            Set<String> plugPackages = PluginManager.getPlugin().getPlugPackages();
            if (testClass != null) {
                plugPackages.add(testClass.getPackage().getName());
            }
            new IocApplicationContext(packages(plugPackages));
            //启动队列
            QueueManager.startQueueServer();
            //启动定时任务
            TaskManager.startTask();
            PluginManager.getPlugin().startedApp();
            //启动完成
            success(args);
            try {
                if (testClass == null) {
                    shutdownLatch.await();
                }
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

    private static Set<String> packages(Set<String> testPackageName){
        Set<String> scanPackage = new HashSet<>();
        if (testPackageName != null) {
            scanPackage.addAll(testPackageName);
        }
        if (mainClass != null) {
            scanPackage.add(mainClass.getPackage().getName());
        }
//        scanPackage.addAll(PlugsManager.getPlugin().getPlugPackages());
        scanPackage.add(HServerApplication.class.getPackage().getName());
        return scanPackage;
    }


    private static void success(String[] args){
        PluginManager.getPlugin().pluginInfo();
        IocApplicationContext.getBeansOfType(InitAdapter.class).forEach(initAdapter -> initAdapter.init(args));
        log.info("HServer启动成功");
        HServerLogAsyncAppender.setHasLog(IocApplicationContext.getBeansOfType(LogAdapter.class));
        IocApplicationContext.getBeansOfType(StartAdapter.class).forEach(StartAdapter::start);
    }

}
