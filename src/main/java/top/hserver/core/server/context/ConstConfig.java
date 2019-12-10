package top.hserver.core.server.context;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class ConstConfig {

    //开启统计
    public static Boolean isStatisticsOpen = false;
    //统计规则
    public static CopyOnWriteArraySet<String> StatisticalRules =new  CopyOnWriteArraySet<>();

    //ip记录
    public static CopyOnWriteArraySet<String> IPData=new CopyOnWriteArraySet<>();
    public static ConcurrentHashMap<String,Long> URIData=new ConcurrentHashMap<>();

}
