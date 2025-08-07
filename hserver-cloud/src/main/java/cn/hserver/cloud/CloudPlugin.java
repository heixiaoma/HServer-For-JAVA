package cn.hserver.cloud;

import cn.hserver.core.plugin.bean.PluginInfo;
import cn.hserver.core.plugin.handler.PluginAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;


public class CloudPlugin extends PluginAdapter {

    private static final Logger log = LoggerFactory.getLogger(CloudPlugin.class);

    @Override
    public PluginInfo getPluginInfo() {
        return new PluginInfo.Builder()
                .name("cloud")
                .description("提供服务器注册发现功能")
                .build();
    }

//    @Override
//    public Set<Class<?>> iocInitBeanList() {
//        Set<Class<?>> classes=new HashSet<>();
//        classes.add(DiscoveryService.class);
//        StartDiscoveryServer.init();
//        return classes;
//    }

}
