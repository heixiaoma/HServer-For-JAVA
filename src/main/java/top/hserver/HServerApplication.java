package top.hserver;

import top.hserver.core.PlugsManager;
import top.hserver.core.queue.QueueDispatcher;
import top.hserver.core.interfaces.InitRunner;
import top.hserver.core.ioc.IocUtil;
import top.hserver.core.ioc.ref.InitBean;
import top.hserver.core.ioc.ref.MemoryInitClass;
import top.hserver.core.log.HServerLogConfig;
import top.hserver.core.properties.PropertiesInit;
import top.hserver.core.server.HServer;
import lombok.extern.slf4j.Slf4j;
import top.hserver.core.server.router.RouterManager;
import top.hserver.core.server.util.EnvironmentUtil;
import top.hserver.core.server.util.PackageUtil;
import top.hserver.core.task.TaskManager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


/**
 * @author hxm
 */
@Slf4j
public class HServerApplication {

    private static Class clazz;
    private static Class mainClass;
    private static String[] packages;

    private static final PlugsManager PLUGS_MANAGER = new PlugsManager();

    /**
     * 添加插件
     *
     * @param plugsClass
     */
    public static void addPlugs(Class... plugsClass) {
        PLUGS_MANAGER.addPlugs(plugsClass);
    }

    /**
     * 启动服务
     *
     * @param port
     * @param args
     */
    public static void run(String[] packageName, Integer port, String... args) {
        iocInit(packageName);
        startServer(port, args);
    }

    /**
     * 主函数启动
     *
     * @param mainClass
     * @param port
     * @param args
     */
    public static void run(Class mainClass, Integer port, String... args) {
        iocInit(mainClass);
        startServer(port, args);
    }

    /**
     * 主函数启动
     *
     * @param mainClass
     * @param port
     */
    public static void run(Class mainClass, Integer port) {
        iocInit(mainClass);
        startServer(port, null);
    }

    /**
     * 启动服务
     *
     * @param port
     * @param args
     */
    public static void run(Integer port, String... args) {
        iocInit();
        startServer(port, args);
    }


    /**
     * 启动服务
     *
     * @param port
     */
    public static void run(String[] packageName, Integer port) {
        iocInit(packageName);
        startServer(port, null);
    }


    /**
     * 启动服务
     *
     * @param port
     */
    public static void run(Integer port) {
        iocInit();
        startServer(port, null);
    }

    /**
     * 非服务模式启动
     *
     * @param args
     */
    public static void run(String[] packageName, String... args) {
        iocInit(packageName);
        initOK(args);
    }


    /**
     * 非服务模式启动
     *
     * @param args
     */
    public static void run(String... args) {
        iocInit();
        initOK(args);
    }

    /**
     * 非服务的测试模式
     *
     * @param testPackageName
     */
    public static void runTest(String testPackageName, Class clazz) {
        iocInit(clazz, null, testPackageName);
        initOK(null);
    }

    /**
     * 服务测试模式
     *
     * @param testPackageName
     * @param port
     */
    public static void runTest(String testPackageName, Integer port, Class clazz) {
        iocInit(clazz, null, testPackageName);
        startServer(port, null);
    }


    private static void startServer(int port, String[] args) {
        try {
            new HServer(port, args).run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void iocInit(String... packages) {
        iocInit(null, null, packages);
    }

    private static void iocInit(Class mainClass) {
        iocInit(null, mainClass);
    }

    private synchronized static void iocInit(Class clazz, Class mainClass, String... packages) {
        HServerApplication.clazz = clazz;
        HServerApplication.mainClass = mainClass;
        HServerApplication.packages = packages;

        /**
         * 初始化哈日志配置
         */
        try {
            EnvironmentUtil.init(clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        HServerLogConfig.init();
        log.info("检查包文件");
        Set<String> scanPackage;
        if (mainClass == null) {
            scanPackage = PackageUtil.scanPackage();
            if (packages != null) {
                scanPackage.addAll(Arrays.asList(packages));
            }
        } else {
            scanPackage = new HashSet<>();
            scanPackage.add(mainClass.getPackage().getName());
        }
        scanPackage.addAll(PLUGS_MANAGER.getPlugPackages());
        scanPackage.add(HServerApplication.class.getPackage().getName());
        log.info("初始化配置文件");
        PropertiesInit.configFile(scanPackage);
        log.info("初始化配置完成");
        log.info("Class动态修改开始...");
        for (String s : scanPackage) {
            MemoryInitClass.init(s);
        }
        log.info("Class动态修改完成");
        log.info("HServer 启动中....");
        log.info("Package 扫描中");
        PLUGS_MANAGER.startIocInit();
        InitBean.init(scanPackage);
        PLUGS_MANAGER.IocInitEnd();
        log.info("IOC 装配中");
        PLUGS_MANAGER.startInjection();
        InitBean.injection();
        PLUGS_MANAGER.injectionEnd();
        //Beetlsql注入
        InitBean.BeetlSqlinit();
        log.info("IOC 全部装配完成");
    }

    private static void initOK(String[] args) {
        //初始化完成可以放开任务了
        TaskManager.IS_OK = true;
        InitRunner bean = IocUtil.getBean(InitRunner.class);
        if (bean != null) {
            bean.init(args);
        }
        QueueDispatcher.startTaskThread();
    }


    /**
     * 重新初始化依赖关系
     */
    public synchronized static void reInitIoc() {
        //IOC清除，
        IocUtil.clearAll();
        //URLMapper 清除
        RouterManager.clearRouterManager();
        //重新加载一盘
        iocInit(HServerApplication.clazz, HServerApplication.mainClass, HServerApplication.packages);
    }

}
