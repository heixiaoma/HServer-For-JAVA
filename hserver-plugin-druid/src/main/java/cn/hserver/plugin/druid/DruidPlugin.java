package cn.hserver.plugin.druid;

import cn.hserver.core.interfaces.PluginAdapter;
import cn.hserver.core.ioc.ref.PackageScanner;

public class DruidPlugin implements PluginAdapter {
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

    }
}
