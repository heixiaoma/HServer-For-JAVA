package cn.hserver.cloud.register;

import cn.hserver.core.plugin.bean.PluginInfo;
import cn.hserver.core.plugin.handler.PluginAdapter;

public class RegisterPlugin extends PluginAdapter {
    @Override
    public PluginInfo getPluginInfo() {
        return new PluginInfo.Builder()
                .name("服务注册")
                .description("提供服务注册能力，让服务发现模块能够快速感知并实现服务调用")
                .build();
    }
}
