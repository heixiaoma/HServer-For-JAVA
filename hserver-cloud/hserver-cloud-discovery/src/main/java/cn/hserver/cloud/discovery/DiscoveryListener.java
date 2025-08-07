package cn.hserver.cloud.discovery;

import cn.hserver.cloud.common.ServerInstance;

import java.util.List;
import java.util.Map;

public interface DiscoveryListener {
    void onChanged(String group, Map<String, List<ServerInstance>> newInstances);
}