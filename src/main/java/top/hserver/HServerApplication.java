package top.hserver;

import top.hserver.cloud.CloudManager;
import top.hserver.core.interfaces.InitRunner;
import top.hserver.core.ioc.IocUtil;
import top.hserver.core.ioc.ref.InitBean;
import top.hserver.core.ioc.ref.MemoryInitClass;
import top.hserver.core.log.HServerLogConfig;
import top.hserver.core.properties.PropertiesInit;
import top.hserver.core.server.HServer;
import lombok.extern.slf4j.Slf4j;
import top.hserver.core.server.context.ConstConfig;
import top.hserver.core.server.util.EnvironmentUtil;
import top.hserver.core.server.util.PackageUtil;
import top.hserver.core.task.TaskManager;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static top.hserver.core.event.EventDispatcher.startTaskThread;


/**
 * @author hxm
 */
@Slf4j
public class HServerApplication {

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
    public static void runTest(String testPackageName,Class clazz) {
        iocInit(clazz,testPackageName);
        initOK(null);
    }

    /**
     * 服务测试模式
     *
     * @param testPackageName
     * @param port
     */
    public static void runTest(String testPackageName, Integer port,Class clazz) {
        iocInit(clazz,testPackageName);
        startServer(port, null);
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

    private static void iocInit(String... packages) {
      iocInit(null,packages);
    }

    private static void iocInit(Class clazz,String... packages) {
        /**
         * 初始化哈日志配置
         */
        EnvironmentUtil.init(clazz);
        new HServerLogConfig().init();
        log.info("检查包文件");
        Set<String> scanPackage = PackageUtil.scanPackage();
        if (packages != null) {
            scanPackage.addAll(Arrays.asList(packages));
        }
        log.info("初始化配置文件");
        PropertiesInit.init();
        log.info("初始化配置完成");
        log.info("Class动态修改开始...");
        for (String s : scanPackage) {
            MemoryInitClass.init(s);
        }
        log.info("Class动态修改完成");
        log.info("HServer 启动中....");
        log.info("Package 扫描中");
        for (String s : scanPackage) {
            InitBean.init(s);
        }
        log.info("IOC 装配中");
        InitBean.injection();
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
        startTaskThread();
    }


}
