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

    private static String getHostIp() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            String lastMatchIP = null;
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr.isLoopbackAddress()) {
                        continue;
                    }
                    lastMatchIP = addr.getHostAddress();
                    if (!lastMatchIP.contains(":")) {
                        return lastMatchIP;// return IPv4 addr
                    }
                }
            }
            if (lastMatchIP != null && lastMatchIP.trim().length() > 0) {
                return InetAddress.getLocalHost().getHostAddress();
            } else {
                return lastMatchIP;
            }
        } catch (Exception e) {
            return "127.0.0.1";
        }
    }
}