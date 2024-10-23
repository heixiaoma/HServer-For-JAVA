package cn.hserver.plugin.web.util;


import java.lang.management.ManagementFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author hxm
 */
public class RequestIdGen {

    private static final AtomicLong lastId = new AtomicLong();
    private static final String HEXIP = hexIp(IpUtil.getLocalIP());
    private static final String PROCESSON = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];


    public static long getRequestCount() {
        return lastId.get();
    }

    public static String getId() {
        long currentTime = System.currentTimeMillis();
        long sequence = lastId.incrementAndGet();
        // 规则： hexIp(ip)-base36(timestamp)-process-seq
        return HEXIP + "-" +
                Long.toString(currentTime, Character.MAX_RADIX) + "-" +
                PROCESSON + "-" + sequence;
    }

    // 将ip转换为定长8个字符的16进制表示形式：255.255.255.255 -> FFFFFFFF
    private static String hexIp(String ip) {
        try {
            StringBuilder sb = new StringBuilder();
            for (String seg : ip.split("\\.")) {
                String h = Integer.toHexString(Integer.parseInt(seg));
                if (h.length() == 1) {
                    sb.append("0");
                }
                sb.append(h);
            }
            return sb.toString();
        } catch (Exception e) {
        }
        //127.0.0.1
        return "7f000001";
    }
}
