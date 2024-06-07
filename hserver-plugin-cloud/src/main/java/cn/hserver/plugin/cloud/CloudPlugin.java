package cn.hserver.plugin.cloud;

import cn.hserver.core.interfaces.PluginAdapter;
import cn.hserver.core.ioc.IocUtil;
import cn.hserver.core.ioc.ref.PackageScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;


public class CloudPlugin implements PluginAdapter {

    private static final Logger log = LoggerFactory.getLogger(CloudPlugin.class);


    @Override
    public void startApp() {

    }

    @Override
    public void startIocInit() {

    }

    @Override
    public Set<Class<?>> iocInitBeanList() {
        Set<Class<?>> classes=new HashSet<>();
        classes.add(DiscoveryService.class);
        StartDiscoveryServer.init();
        return classes;
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

    }
}
