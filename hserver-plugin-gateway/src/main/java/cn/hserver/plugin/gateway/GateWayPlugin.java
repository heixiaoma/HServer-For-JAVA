package cn.hserver.plugin.gateway;

import cn.hserver.core.interfaces.PluginAdapter;
import cn.hserver.core.ioc.ref.PackageScanner;
import cn.hserver.core.server.util.PropUtil;
import cn.hserver.plugin.gateway.config.GateWayConfig;
import cn.hserver.plugin.gateway.enums.GatewayMode;

public class GateWayPlugin implements PluginAdapter {
    @Override
    public void startApp() {
        GatewayMode mode = GatewayMode.getMode(PropUtil.getInstance().get("gateway.mode"));
        if (mode != null) {
            GateWayConfig.GATEWAY_MODE = mode;
        }
        Integer port = PropUtil.getInstance().getInt("gateway.port");
        if (port!=null){

        }
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
