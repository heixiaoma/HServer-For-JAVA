package top.hserver.core.server.context;

import io.netty.handler.ssl.SslContext;

import java.util.concurrent.CopyOnWriteArraySet;

public class ConstConfig {

  /**
   * 版本号
   */
  public static final String VERSION = "2.9.26";
  /**
   * 定时任务线程数配置
   */
  public static Integer taskPool = Runtime.getRuntime().availableProcessors() + 1;
  /**
   * SSL 配置
   */
  public static SslContext sslContext=null;

  /**
   * git 地址反馈
   */
  public static final String BUG_ADDRESS ="https://gitee.com/heixiaomas_admin/HServer/issues";

  /**
   * 社区地址
   */
  public static final String communityAddress="http://hserver.top";

}
