package cn.hserver.plugin.mqtt;

import cn.hserver.core.interfaces.PluginAdapter;
import cn.hserver.core.ioc.IocUtil;
import cn.hserver.core.ioc.ref.PackageScanner;
import cn.hserver.core.server.util.ExceptionUtil;
import cn.hserver.plugin.mqtt.interfaces.MqttAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MqttPlugin implements PluginAdapter {
    private static final Logger log = LoggerFactory.getLogger(MqttPlugin.class);

    @Override
    public void startApp() {

    }
    @Override
    public void startIocInit() {

    }

    @Override
    public boolean iocInitBean(Class aClass) {
        try {
            //检测这个Bean是否是Mqtt的
            if (MqttAdapter.class.isAssignableFrom(aClass)) {
                IocUtil.addBean(MqttAdapter.class.getName(), aClass.newInstance());
                return true;
            }
        }catch (Exception e){
            log.error(ExceptionUtil.getMessage(e));
        }
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

    }
}
