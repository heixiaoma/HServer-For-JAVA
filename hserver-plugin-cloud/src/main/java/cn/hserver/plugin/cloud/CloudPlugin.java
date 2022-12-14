package cn.hserver.plugin.cloud;

import cn.hserver.core.interfaces.PluginAdapter;
import cn.hserver.core.ioc.ref.PackageScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CloudPlugin implements PluginAdapter {

    private static final Logger log = LoggerFactory.getLogger(CloudPlugin.class);


    @Override
    public void startApp() {

    }

    @Override
    public void startIocInit() {

    }

    @Override
    public boolean iocInitBean(Class classz) {
        return false;
    }

    @Override
    public void iocInit(PackageScanner packageScanner) {

    }

    @Override
    public void iocInitEnd() {

    }

    @Override
    public void startInjection() {

    }

    @Override
    public void injectionEnd() {
        StartDiscoveryServer.init();
        log.info("cloud 启动完成");
    }
}
