package cn.hserver.core.config;

import java.io.File;

/**
 * @author hxm
 */
public class ConstConfig {
    /**
     * 内部自用名字
     */
    public final static String SERVER_NAME = "HServer";
    /**
     * 版本
     */
    public final static String VERSION = "4.0.0-beta.3";
    /**
     * 当前项目路径
     */
    public static final String PATH = System.getProperty("user.dir") + File.separator;
    /**
     * 运行环境
     */
    public static Boolean RUN_JAR = false;
    /**
     * classpat路径
     */
    public static String CLASSPATH;
    /**
     * 加密密码
     */
    public static String PASSWORD;

    /**
     * 定时任务线程数配置
     */
    public static Integer taskPool = Runtime.getRuntime().availableProcessors() + 1;


    /**
     * 配置文件
     */
    public static String EVN = System.getProperty("env");


    /**
     * 持久化文件存储位置
     */
    public static String PERSIST_PATH = PATH + "queue";


    /**
     * 应用名字
     */
    public  static String APP_NAME = "HServer";

    public static String LOGBACK_NAME = "logback-hserver.xml";

    public static String LOG_LEVEL = "debug";

}
