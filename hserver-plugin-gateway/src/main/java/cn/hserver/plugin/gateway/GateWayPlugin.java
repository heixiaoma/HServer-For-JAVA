package cn.hserver.plugin.gateway;

import cn.hserver.core.interfaces.PluginAdapter;
import cn.hserver.core.ioc.IocUtil;
import cn.hserver.core.ioc.ref.PackageScanner;
import cn.hserver.core.server.util.PropUtil;
import cn.hserver.plugin.gateway.business.Business;
import cn.hserver.plugin.gateway.config.GateWayConfig;
import cn.hserver.plugin.gateway.enums.GatewayMode;

import java.util.ArrayList;
import java.util.List;

public class GateWayPlugin implements PluginAdapter {
    @Override
    public void startApp() {
        GatewayMode mode = GatewayMode.getMode(PropUtil.getInstance().get("gateway.mode"));
        if (mode != null) {
            GateWayConfig.GATEWAY_MODE = mode;
        }
        String port = PropUtil.getInstance().get("gateway.port");
        if (port != null && port.trim().length() > 0) {
            String[] split = port.split(",");
            List<Integer> ports = new ArrayList<>();
            for (String s : split) {
                ports.add(Integer.parseInt(s));
            }
            GateWayConfig.PORT = ports;
        }
    }

    @Override
    public void startIocInit() {

    }

    @Override
    public boolean iocInitBean(Class aClass) {
        try {
            //检测这个Bean是否是Business的子类类
            if (Business.class.isAssignableFrom(aClass)) {
                IocUtil.addListBean(Business.class.getName(), aClass.newInstance());
                return true;
            }
        } catch (Exception e) {
           throw new RuntimeException(e);
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
