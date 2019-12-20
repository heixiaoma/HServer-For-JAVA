package top.hserver.core.server.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import top.hserver.core.server.stat.IpData;
import top.hserver.core.server.stat.RequestData;

import java.net.InetSocketAddress;
import java.util.Deque;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 统计
 */
public class StatisticsHandler {

    private static final Map<String, IpData> ipMap = new ConcurrentHashMap<>(); //计算每个请求
    private static final Deque<RequestData> logRequestQue = new ConcurrentLinkedDeque<>(); // 最近50个请求队列
    private static final AtomicLong uniqueIpCount = new AtomicLong(); // 唯一IP请求的数量 uv
    private static final AtomicLong count = new AtomicLong(); // 请求总数   pv
    private static final ConcurrentHashMap<String, Long> uriData = new ConcurrentHashMap<>();     //uri记录


    public void addToConnectionDeque(ChannelHandlerContext ctx, String url, long consumeTime) {
        //获取ChannelTrafficShapingHandler的实例以进行带宽监视
        ChannelTrafficShapingHandler ch = (ChannelTrafficShapingHandler) ctx.channel().pipeline().get("统计");
        ch.trafficCounter().stop();  //Stop the monitoring process
//        if (logRequestQue.size() < 50) {
//            logRequestQue.addLast(new RequestData(getClientIp(ctx), url, ch.trafficCounter().cumulativeWrittenBytes(),
//                    ch.trafficCounter().cumulativeReadBytes(), ch.trafficCounter().lastWriteThroughput(),consumeTime));
//        } else {
//            logRequestQue.removeFirst();
//            logRequestQue.addLast(new RequestData(getClientIp(ctx), url, ch.trafficCounter().cumulativeWrittenBytes(),
//                    ch.trafficCounter().cumulativeReadBytes(), ch.trafficCounter().lastWriteThroughput(),consumeTime));
//        }

        logRequestQue.addLast(new RequestData(getClientIp(ctx), url, ch.trafficCounter().cumulativeWrittenBytes(),
                ch.trafficCounter().cumulativeReadBytes(), ch.trafficCounter().lastWriteThroughput(), consumeTime));
        ch.trafficCounter().resetCumulativeTime(); //重置读写累积字节计数器以及通道计数器的关联时间
    }

    //总连接数的原子增量
    public void increaseCount() {
        count.incrementAndGet();
    }

    //统计URI访问数
    public void uriDataCount(String uri) {
        synchronized (uriData) {
            Long uriCount = uriData.get(uri);
            //统计页面数，页面总和就是总访问数
            if (uriCount == null) {
                uriData.put(uri, 1L);
            } else {
                uriData.put(uri, uriCount + 1);
            }
        }
    }


    //调用此方法以计算每个请求
    public void addToIpMap(ChannelHandlerContext ctx) {
        String clientIP = ((InetSocketAddress) ctx.channel().remoteAddress()).getHostString();
        synchronized (ipMap) {
            if (!ipMap.containsKey(clientIP)) {//如果IP是新的->将其放在地图中，默认计数为1，当前时间为
                ipMap.put(clientIP, new IpData());
                uniqueIpCount.incrementAndGet();
            } else { // 如果IP不是新的->更新时间和增量计数
                ipMap.get(clientIP).incrementCount();
                ipMap.get(clientIP).updateTime(); // 为了知道最后一次请求的时间
            }
        }
    }

    public String getClientIp(ChannelHandlerContext ctx) {
        return ((InetSocketAddress) ctx.channel().remoteAddress()).getHostString();
    }

    // ---- getters ----
    public static Map<String, IpData> getIpMap() {
        return ipMap;
    }

    public static Deque<RequestData> getLogRequestQue() {
        return logRequestQue;
    }

    public static AtomicLong getCount() {
        return count;
    }

    public static AtomicLong getUniqueIpCount() {
        return uniqueIpCount;
    }

    public static ConcurrentHashMap<String, Long> getUriData() {
        return uriData;
    }


    //----remove---
    public static Map<String, IpData> removeIpMap() {
        synchronized (ipMap) {
            Map<String, IpData> tmpIpMap = new ConcurrentHashMap<>();
            tmpIpMap.putAll(ipMap);
            ipMap.clear();
            return tmpIpMap;
        }
    }
    public static Deque<RequestData> removeLogRequestQue() {
        synchronized (logRequestQue) {
            Deque<RequestData> tmpLogRequestQue = new ConcurrentLinkedDeque<>();
            tmpLogRequestQue.addAll(logRequestQue);
            logRequestQue.clear();
            return tmpLogRequestQue;
        }
    }
    public static AtomicLong removeCount() {
        synchronized (count) {
            AtomicLong atomicLong = new AtomicLong(count.get());
             count.set(0);
            return atomicLong;
        }
    }
    public static AtomicLong removeUniqueIpCount() {
        synchronized (uniqueIpCount) {
            AtomicLong atomicLong = new AtomicLong(uniqueIpCount.get());
            uniqueIpCount.set(0);
            return atomicLong;
        }
    }
    public static ConcurrentHashMap<String, Long> removeUriData() {
        synchronized (uriData) {
            ConcurrentHashMap<String, Long> tmpIpMap = new ConcurrentHashMap<>();
            tmpIpMap.putAll(uriData);
            uriData.clear();
            return tmpIpMap;
        }
    }
}
