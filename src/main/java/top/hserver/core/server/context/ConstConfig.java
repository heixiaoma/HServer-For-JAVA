package top.hserver.core.server.context;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.ssl.SslContext;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import top.hserver.core.server.util.NamedThreadFactory;

import java.io.File;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

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
     * 当可以使用Epoll时是否使用Epoll
     */
    public static Boolean EPOLL = true;

    /**
     * 版本号
     */
    public static final String VERSION = "2.9.49";
    /**
     * 定时任务线程数配置
     */
    public static Integer taskPool = Runtime.getRuntime().availableProcessors() + 1;

   /**
     * SSL 配置
     */
    public static SslContext sslContext = null;

    /**
     * git 地址反馈
     */
    public static final String BUG_ADDRESS = "https://gitee.com/HServer/HServer/issues";

    /**
     * 社区地址
     */
    public static final String COMMUNITY_ADDRESS = "https://gitee.com/HServer/HServer/wikis/pages";


    /**
     * webServer  bossThreadCount
     */

    public static Integer bossPool = 2;

    /**
     * webServer workerGroupThreadCount
     */

    public static Integer workerPool = 4;

    /**
     * 对象处理
     */
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
            .configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);

    /**
     * 另外JSON一个名字，兼容以前的
     */
    public static final ObjectMapper JSON = OBJECT_MAPPER;

    /**
     * 配置文件
     */
    public static String profiles = System.getProperty("env");

    /**
     * 业务线程池子
     */
    public static EventExecutorGroup BUSINESS_EVENT;
}
