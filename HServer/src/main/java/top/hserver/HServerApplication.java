package top.hserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hserver.core.interfaces.ReInitRunner;
import top.hserver.core.plugs.PlugsManager;
import top.hserver.core.queue.QueueDispatcher;
import top.hserver.core.interfaces.InitRunner;
import top.hserver.core.ioc.IocUtil;
import top.hserver.core.ioc.ref.InitBean;
import top.hserver.core.ioc.ref.MemoryInitClass;
import top.hserver.core.log.HServerLogConfig;
import top.hserver.core.properties.PropertiesInit;
import top.hserver.core.server.HServer;
import top.hserver.core.server.context.ConstConfig;
import top.hserver.core.server.json.JsonAdapter;
import top.hserver.core.server.router.RouterManager;
import top.hserver.core.server.util.EnvironmentUtil;
import top.hserver.core.server.util.ExceptionUtil;
import top.hserver.core.server.util.PackageUtil;
import top.hserver.core.task.TaskManager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * @author hxm
 */
public class HServerApplication {
    private static final Logger log = LoggerFactory.getLogger(HServerApplication.class);
    private static Class clazz;
    private static Class mainClass;
    private static String[] packages;

    private static final PlugsManager PLUGS_MANAGER = new PlugsManager();

    /**
     * 添加插件
     *
     * @param plugsClass
     */
    @Deprecated
    public static void addPlugins(Class... plugsClass) {
        PLUGS_MANAGER.addPlugins(plugsClass);
    }

    /**
     * 启动服务
     *
     * @param port
     * @param args
     */
    public static void run(String[] packageName, Integer port, String... args) {
        iocInit(packageName);
        startServer(new Integer[]{port}, args);
    }

    public static void run(String[] packageName, Integer[] ports, String... args) {
        iocInit(packageName);
        startServer(ports, args);
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
        startServer(new Integer[]{port}, args);
    }


    public static void run(Class mainClass, Integer[] ports, String... args) {
        iocInit(mainClass);
        startServer(ports, args);
    }

    /**
     * 主函数启动
     *
     * @param mainClass
     * @param port
     */
    public static void run(Class mainClass, Integer port) {
        iocInit(mainClass);
        startServer(new Integer[]{port}, null);
    }

    /**
     * 启动服务
     *
     * @param port
     * @param args
     */
    public static void run(Integer port, String... args) {
        iocInit();
        startServer(new Integer[]{port}, args);
    }


    /**
     * 启动服务
     *
     * @param port
     */
    public static void run(String[] packageName, Integer port) {
        iocInit(packageName);
        startServer(new Integer[]{port}, null);
    }


    /**
     * 启动服务
     *
     * @param port
     */
    public static void run(Integer port) {
        iocInit();
        startServer(new Integer[]{port}, null);
    }

    /**
     * 非服务模式启动
     *
     * @param args
     */
    public static void run(String[] packageName, String... args) {
        iocInit(packageName);
        initTestOk(args);
    }


    /**
     * 非服务模式启动
     *
     * @param args
     */
    public static void run(String... args) {
        iocInit();
        initTestOk(args);
    }

    /**
     * 非服务的测试模式
     *
     * @param testPackageName
     */
    public static void runTest(String testPackageName, Class clazz) {
        iocInit(clazz, null, testPackageName);
        initTestOk(null);
    }

    /**
     * 服务测试模式
     *
     * @param testPackageName
     * @param port
     */
    public static void runTest(String testPackageName, Integer port, Class clazz) {
        iocInit(clazz, null, testPackageName);
        startServer(new Integer[]{port}, null);
    }


    private static void startServer(Integer[] port, String[] args) {
        try {
            new HServer(port, args).run();
        } catch (Exception e) {
            log.error(ExceptionUtil.getMessage(e));
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
            log.error(ExceptionUtil.getMessage(e));
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
        MemoryInitClass.closeCache();
        for (String s : scanPackage) {
            MemoryInitClass.init(s);
        }
        log.info("Class动态修改完成");
        log.info("HServer 启动中....");
        log.info("Package 扫描中");
        PLUGS_MANAGER.startIocInit();
        InitBean.init(scanPackage);
        PLUGS_MANAGER.iocInitEnd();
        log.info("IOC 装配中");
        PLUGS_MANAGER.startInjection();
        InitBean.injection();
        PLUGS_MANAGER.injectionEnd();
        log.info("IOC 全部装配完成");
    }

    private static void initTestOk(String[] args) {
        //初始化完成可以放开任务了
        TaskManager.IS_OK = true;
        QueueDispatcher.startTaskThread();
        List<InitRunner> listBean = IocUtil.getListBean(InitRunner.class);
        if (listBean != null) {
            for (InitRunner initRunner : listBean) {
                initRunner.init(args);
            }
        }
    }

    /**
     * 重新初始化依赖关系
     */
    public synchronized static void reInitIoc() {
        List<ReInitRunner> listBean = IocUtil.getListBean(ReInitRunner.class);
        if (listBean != null) {
            for (ReInitRunner reInitRunner : listBean) {
                reInitRunner.reInit();
            }
        }
        //IOC清除，
        IocUtil.clearAll();
        //URLMapper 清除
        RouterManager.clearRouterManager();
        //重新加载一盘
        iocInit(HServerApplication.clazz, HServerApplication.mainClass, HServerApplication.packages);
    }


    public static void setJson(JsonAdapter jsonAdapter){
        ConstConfig.JSONADAPTER=jsonAdapter;
    }

}
