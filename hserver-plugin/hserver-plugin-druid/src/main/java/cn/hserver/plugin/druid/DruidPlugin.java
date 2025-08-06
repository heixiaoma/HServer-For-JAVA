package cn.hserver.plugin.druid;


import cn.hserver.core.plugin.bean.PluginInfo;
import cn.hserver.core.plugin.handler.PluginAdapter;


public class DruidPlugin extends PluginAdapter {

    @Override
    public PluginInfo getPluginInfo() {
        return new PluginInfo.Builder()
                .name("Druid")
                .description("Druid 是一个 JDBC 组件库，包含数据库连接池，安全检查，防火墙等功能")
                .build();
    }
}
