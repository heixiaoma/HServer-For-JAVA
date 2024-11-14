package cn.hserver.plugin.mqtt;

import cn.hserver.core.interfaces.PluginAdapter;
import cn.hserver.core.ioc.IocUtil;
import cn.hserver.core.ioc.ref.PackageScanner;
import cn.hserver.core.server.util.ExceptionUtil;
import cn.hserver.plugin.mqtt.interfaces.MqttAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class MqttPlugin implements PluginAdapter {
    private static final Logger log = LoggerFactory.getLogger(MqttPlugin.class);

    @Override
    public void startApp() {

    }
    @Override
    public void startIocInit() {

    }

    @Override
    public Set<Class<?>> iocInitBeanList() {
        return null;
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
