package top.hserver.core.server.context;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.ssl.SslContext;

import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author hxm
 */
public class ConstConfig {

  /**
   * 运行环境
   */
  public static Boolean RUNJAR=false;
  /**
   * classpat路径
   */
  public static String CLASSPATH;

  /**
   * 版本号
   */
  public static final String VERSION = "2.9.35";
  /**
   * 定时任务线程数配置
   */
  public static Integer taskPool = Runtime.getRuntime().availableProcessors() + 1;

  /**
   * 队列里面的线程数
   */
  public static Integer queuePool = Runtime.getRuntime().availableProcessors() + 1;
  /**
   * SSL 配置
   */
  public static SslContext sslContext=null;

  /**
   * git 地址反馈
   */
  public static final String BUG_ADDRESS ="https://gitee.com/HServer/HServer/issues";

  /**
   * 社区地址
   */
  public static final String COMMUNITY_ADDRESS ="http://hserver.top";


  /**
   * webServer  bossThreadCount
   */

  public static Integer bossPool=2;

  /**
   * webServer workerGroupThreadCount
   */

  public static Integer workerPool=4;

  public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
    .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

}
