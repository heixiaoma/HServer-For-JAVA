package cn.hserver.plugin.forest;

import cn.hserver.core.context.IocApplicationContext;
import cn.hserver.core.plugin.bean.PluginInfo;
import cn.hserver.core.plugin.handler.PluginAdapter;
import cn.hserver.plugin.forest.config.ForestClientConfig;
import com.dtflys.forest.Forest;
import com.dtflys.forest.annotation.ForestClient;

import java.util.List;

public class ForestPlugin extends PluginAdapter {


    @Override
    public void iocStartScan(Class<?> clazz) {
        if (clazz.isAnnotationPresent(ForestClient.class)) {
            Object data = Forest.client(clazz);
            if (data != null) {
                IocApplicationContext.addBean(data);
            }
        }
    }

    @Override
    public void startedApp() {
        List<ForestClientConfig> beansOfType = IocApplicationContext.getBeansOfType(ForestClientConfig.class);
        if (beansOfType != null && !beansOfType.isEmpty()) {
            for (ForestClientConfig bean : beansOfType) {
                bean.config(Forest.config());
            }
        }
    }

    @Override
    public PluginInfo getPluginInfo() {
        return new PluginInfo.Builder()
                .name("Forest插件")
                .description("一个高级、轻量级的 Java 声明式 HTTP 客户端框架")
                .build();
    }
}
