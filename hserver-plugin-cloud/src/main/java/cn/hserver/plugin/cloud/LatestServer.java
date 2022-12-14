package cn.hserver.plugin.cloud;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LatestServer {
    Map<String, DynamicRoundRobin> S_DATA = new ConcurrentHashMap<>();
}
