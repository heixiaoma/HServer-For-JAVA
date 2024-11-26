package cn.hserver.core.server.context;

import java.io.File;

/**
 * @author hxm
 */
public class ConstConfig {
    /**
     * 当前项目路径
     */
    public static final String PATH = System.getProperty("user.dir") + File.separator;
    /**
     * 运行环境
     */
    public static Boolean RUNJAR = false;
    /**
     * classpat路径
     */
    public static String CLASSPATH;

    /**
     * 定时任务线程数配置
     */
    public static Integer taskPool = Runtime.getRuntime().availableProcessors() + 1;


    /**
     * webServer workerGroupThreadCount
     */
    public static Integer workerPool = 0;

    /**
     * backlog 指定了内核为此套接口排队的最大连接个数；
     * 对于给定的监听套接口，内核要维护两个队列: 未连接队列和已连接队列
     * backlog 的值即为未连接队列和已连接队列的和。
     */
    public static Integer backLog = 8192;

    /**
     * 默认端口
     */
    public static Integer[] PORTS = new Integer[]{8888};

    /**
     * 配置文件
     */
    public static String profiles = System.getProperty("env");


    /**
     * 持久化文件存储位置
     */
    public static String PERSIST_PATH = PATH + "queue";


    /**
     * 内部自用名字
     */
    public final static String SERVER_NAME = "HServer";

    public final static String VERSION = "3.6.M3";

    /**
     * 用户自定义的服务名
     */
    public static String APP_NAME = "HServer";

    /**
     * hum 默认消息端口
     */
    public static Integer HUM_PORT = 9527;

    /**
     * 是否开启追踪
     */
    public static Boolean TRACK = false;

    /**
     * 可以epoll时是否使用
     */
    public static IoMultiplexer IO_MOD = IoMultiplexer.DEFAULT;

    /**
     * 跟踪扩展包
     */
    public static String[] TRACK_EXT_PACKAGES = new String[0];

    /**
     * 不跟踪的包
     */
    public static String[] TRACK_NO_PACKAGES = new String[0];

    /**
     * 前置以协议大小
     */
    public static Integer PRE_PROTOCOL_MAX_SIZE = 4096;


    /**
     * 是否开启HUM消息
     */
    public static Boolean HUM_OPEN = true;
}
