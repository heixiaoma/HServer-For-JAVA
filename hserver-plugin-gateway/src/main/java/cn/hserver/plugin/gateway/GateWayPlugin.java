package cn.hserver.plugin.gateway;

import cn.hserver.core.interfaces.PluginAdapter;
import cn.hserver.core.ioc.IocUtil;
import cn.hserver.core.ioc.ref.PackageScanner;
import cn.hserver.core.server.util.PropUtil;
import cn.hserver.plugin.gateway.business.Business;
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
        if (port != null) {

        }
    }

    @Override
    public void startIocInit() {

    }

    @Override
    public boolean iocInitBean(Class aClass) {
        try {
            //检测这个Bean是否是全局异常处理的类
            if (Business.class.isAssignableFrom(aClass)) {
                IocUtil.addListBean(Business.class.getName(), aClass.newInstance());
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
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
