package cn.hserver.plugin.mqtt;

import cn.hserver.core.interfaces.PluginAdapter;
import cn.hserver.core.ioc.IocUtil;
import cn.hserver.core.ioc.ref.PackageScanner;
import cn.hserver.plugin.mqtt.interfaces.MqttAdapter;

public class MqttPlugin implements PluginAdapter {

    @Override
    public void startIocInit() {

    }

    @Override
    public boolean iocInitBean(Class aClass) {
        //检测这个Bean是否是Mqtt的
        if (MqttAdapter.class.isAssignableFrom(aClass)) {
            IocUtil.addBean(MqttAdapter.class.getName(), aClass.newInstance());
            return true;
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
