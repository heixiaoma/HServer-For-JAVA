package top.hserver.core.server.context;

import ch.qos.logback.classic.Level;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.ssl.SslContext;
import io.netty.util.concurrent.EventExecutorGroup;

import java.io.File;

import static com.fasterxml.jackson.databind.DeserializationFeature.*;

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
    public static final String VERSION = "2.9.73";
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
     * rpcTimeOut rpc超时时间设置
     */

    public static Integer rpcTimeOut = 5000;

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
            .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
            .configure(ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
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

    /**
     * 持久化文件存储位置
     */
    public static String PERSIST_PATH = PATH + "queue";

    /**
     * 流量整形
     */
    public static Long WRITE_LIMIT = null;

    public static Long READ_LIMIT = null;

    /**
     * 默认消息大小
     */
    public static Integer HTTP_CONTENT_SIZE=Integer.MAX_VALUE;

}
