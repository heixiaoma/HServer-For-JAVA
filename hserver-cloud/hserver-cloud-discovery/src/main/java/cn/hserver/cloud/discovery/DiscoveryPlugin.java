package cn.hserver.cloud.discovery;

import cn.hserver.core.plugin.bean.PluginInfo;
import cn.hserver.core.plugin.handler.PluginAdapter;

public class DiscoveryPlugin extends PluginAdapter {
    @Override
    public PluginInfo getPluginInfo() {
        return new PluginInfo.Builder()
                .name("服务发现")
                .description("提供服务发现能力，让你快速查询相关服务配置")
                .build();
    }
}
