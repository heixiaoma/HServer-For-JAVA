package top.hserver.core.server.util;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author hxm
 */
public class RequestIdGen {

    private static AtomicLong lastId = new AtomicLong();
    private static final String HEXIP = hexIp(getHostIp());
    private static final String PROCESSON = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];

    public static String getId() {
        // 规则： hexIp(ip)-base36(timestamp)-process-seq
        return HEXIP +
                "-" +
                Long.toString(System.currentTimeMillis(), Character.MAX_RADIX) +
                "-" +
                PROCESSON +
                "-" +
                lastId.incrementAndGet();
    }

    // 将ip转换为定长8个字符的16进制表示形式：255.255.255.255 -> FFFFFFFF
    private static String hexIp(String ip) {
        StringBuilder sb = new StringBuilder();
        for (String seg : ip.split("\\.")) {
            String h = Integer.toHexString(Integer.parseInt(seg));
            if (h.length() == 1) {
                sb.append("0");
            }
            sb.append(h);
        }
        return sb.toString();
    }

    private static String getHostIp() {
        try {
            for (Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces(); interfaces.hasMoreElements(); ) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface.isLoopback() || networkInterface.isVirtual() || !networkInterface.isUp()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                if (addresses.hasMoreElements()) {
                    return addresses.nextElement().getHostAddress();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "127.0.0.1";
    }
}