package cn.hserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.hserver.core.plugs.PlugsManager;
import cn.hserver.core.queue.QueueDispatcher;
import cn.hserver.core.interfaces.InitRunner;
import cn.hserver.core.ioc.IocUtil;
import cn.hserver.core.ioc.ref.InitBean;
import cn.hserver.core.ioc.ref.MemoryInitClass;
import cn.hserver.core.log.HServerLogConfig;
import cn.hserver.core.properties.PropertiesInit;
import cn.hserver.core.server.HServer;
import cn.hserver.core.server.context.ConstConfig;
import cn.hserver.core.server.json.JsonAdapter;
import cn.hserver.core.server.util.EnvironmentUtil;
import cn.hserver.core.server.util.ExceptionUtil;
import cn.hserver.core.server.util.PackageUtil;
import cn.hserver.core.task.TaskManager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static cn.hserver.core.server.context.ConstConfig.TRACK_EXT_PACKAGES;


/**
 * @author hxm
 */
public class HServerApplication {
    private static final Logger log = LoggerFactory.getLogger(HServerApplication.class);

    public static Class<?> mainClass;

    private static final PlugsManager PLUGS_MANAGER = new PlugsManager();

    //单端口
    public static void run(Class<?> mainClass, Integer port, String... args) {
        ConstConfig.PORTS = new Integer[]{port};
        iocInit(mainClass);
        startServer(args);
    }

    //多端口
    public static void run(Class<?> mainClass, Integer[] ports, String... args) {
        ConstConfig.PORTS = ports;
        iocInit(mainClass);
        startServer(args);
    }

    //无端口，默认端口，或者配置端口
    public static void run(Class<?> mainClass, String... args) {
        iocInit(mainClass);
        startServer(args);
    }

    /**
     * 非服务的测试模式
     */
    public static void runTest(String testPackageName, Class<?> clazz) {
        iocInit(clazz, null, testPackageName);
        initTestOk();
    }


    private static void startServer(String[] args) {
        try {
            new HServer(ConstConfig.PORTS, args).run();
        } catch (Exception e) {
            log.error(ExceptionUtil.getMessage(e));
        }
    }


    private static void iocInit(Class<?> mainClass) {
        iocInit(null, mainClass);
    }

    private synchronized static void iocInit(Class<?> clazz, Class<?> mainClass, String... packages) {
        HServerApplication.mainClass = mainClass;
        //初始化哈日志配置
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
        PropertiesInit.configFile();
        log.info("初始化配置完成");
        log.info("Class动态修改开始...");
        //没开启追踪的不追踪
        if (ConstConfig.TRACK) {
            MemoryInitClass.closeCache();
            //默认的
            for (String s : scanPackage) {
                MemoryInitClass.init(s);
            }
            //扩展的
            if (TRACK_EXT_PACKAGES != null && TRACK_EXT_PACKAGES.length > 0) {
                for (String extPackage : TRACK_EXT_PACKAGES) {
                    MemoryInitClass.init(extPackage);
                }
            }
            MemoryInitClass.closeCache();
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

    private static void initTestOk() {
        //初始化完成可以放开任务了
        TaskManager.IS_OK = true;
        QueueDispatcher.startTaskThread();
        List<InitRunner> listBean = IocUtil.getListBean(InitRunner.class);
        if (listBean != null) {
            for (InitRunner initRunner : listBean) {
                initRunner.init(null);
            }
        }
    }

    public static void setJson(JsonAdapter jsonAdapter) {
        ConstConfig.JSONADAPTER = jsonAdapter;
    }

}
