package cn.hserver;

import cn.hserver.core.interfaces.LogAdapter;
import cn.hserver.core.ioc.ref.InitIoc;
import cn.hserver.core.log.HServerLogAsyncAppender;
import io.netty.channel.ChannelOption;
import io.netty.util.ResourceLeakDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.hserver.core.plugs.PlugsManager;
import cn.hserver.core.queue.QueueDispatcher;
import cn.hserver.core.interfaces.InitRunner;
import cn.hserver.core.ioc.IocUtil;
import cn.hserver.core.ioc.ref.MemoryInitClass;
import cn.hserver.core.log.HServerLogConfig;
import cn.hserver.core.properties.PropertiesInit;
import cn.hserver.core.server.HServer;
import cn.hserver.core.server.context.ConstConfig;
import cn.hserver.core.server.util.EnvironmentUtil;
import cn.hserver.core.server.util.ExceptionUtil;
import cn.hserver.core.server.util.PackageUtil;
import cn.hserver.core.task.TaskManager;

import java.util.*;

import static cn.hserver.core.server.context.ConstConfig.TRACK_EXT_PACKAGES;


/**
 * @author hxm
 */
public class HServerApplication {
    private static final Logger log = LoggerFactory.getLogger(HServerApplication.class);
    private static final Map<ChannelOption<Object>, Object> TCP_OPTIONS = new HashMap<>();
    private static final Map<ChannelOption<Object>, Object> TCP_CHILD_OPTIONS = new HashMap<>();

    public static Class<?> mainClass;
    public static ResourceLeakDetector.Level level= ResourceLeakDetector.Level.DISABLED;

    /**
     * 添加一些netty options选项
     * @param option
     * @param value
     */
    public static <T> void addTcpOptions(ChannelOption<T> option, T value) {
        TCP_OPTIONS.put((ChannelOption)option, value);
    }

    public static <T> void addTcpChildOptions(ChannelOption<T> option, T value) {
        TCP_CHILD_OPTIONS.put((ChannelOption)option, value);
    }
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
        initOk(null);
    }


    private static void startServer(String[] args) {
        try {
            initOk(args);
            new HServer(ConstConfig.PORTS).run(TCP_OPTIONS,TCP_CHILD_OPTIONS);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
    }


    private static void iocInit(Class<?> mainClass) {
        iocInit(null, mainClass);
    }

    private synchronized static void iocInit(Class<?> clazz, Class<?> mainClass, String... packages) {
        HServerApplication.mainClass = mainClass;
        PropertiesInit.configFile();
        //初始化哈日志配置
        try {
            EnvironmentUtil.init(clazz);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return;
        }
        PlugsManager.getPlugin().startApp();
        HServerLogConfig.init();
        ResourceLeakDetector.setLevel(level);
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
        scanPackage.addAll(PlugsManager.getPlugin().getPlugPackages());
        scanPackage.add(HServerApplication.class.getPackage().getName());
        log.info("Class动态修改开始...");
        //没开启追踪的不追踪
        if (ConstConfig.TRACK) {
            MemoryInitClass.closeCache();
            //默认的
            for (String s : scanPackage) {
                MemoryInitClass.init(s);
            }
            //扩展的
            if (TRACK_EXT_PACKAGES != null) {
                for (String extPackage : TRACK_EXT_PACKAGES) {
                    MemoryInitClass.init(extPackage);
                }
            }
            MemoryInitClass.closeCache();
        }
        log.info("Class动态修改完成");
        log.info("HServer 启动中....");
        log.info("Package 扫描中");
        PlugsManager.getPlugin().startIocInit();
        InitIoc.init(PackageUtil.deduplication(scanPackage));
        PlugsManager.getPlugin().iocInitEnd();
        log.info("IOC 装配中");
        PlugsManager.getPlugin().startInjection();
        InitIoc.injection();
        PlugsManager.getPlugin().injectionEnd();
        log.info("IOC 全部装配完成");
    }


    private static void initOk(String[] args) {
        HServerLogAsyncAppender.setHasLog(IocUtil.getListBean(LogAdapter.class));
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
}
