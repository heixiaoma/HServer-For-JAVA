package test8;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class HClient {

    private final static ConcurrentMap<String, HConnection> POOL = new ConcurrentHashMap<>();

    public static HRequest connect(String host, int port) {
        String key = host + port;
        HConnection hConnection = POOL.get(key);
        if (hConnection != null && hConnection.isActive()) {
            return new HReq(hConnection);
        }
        HConnection hConnection1 = new HConnection(host, port);
        POOL.remove(key);
        POOL.put(key, hConnection1);
        return new HReq(hConnection1);
    }
}
