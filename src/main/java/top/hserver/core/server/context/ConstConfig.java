package top.hserver.core.server.context;

import java.util.concurrent.CopyOnWriteArraySet;

public class ConstConfig {

    //开启统计
    public static Boolean isStatisticsOpen = false;
    //统计规则
    public static final CopyOnWriteArraySet<String> StatisticalRules = new CopyOnWriteArraySet<>();
    //版本号
    public static final String version = "2.9.4";
    //定时任务线程数配置
    public static Integer taskPool = Runtime.getRuntime().availableProcessors() + 1;
}
