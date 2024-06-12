package cn.hserver.plugin.gateway;

import cn.hserver.HServerApplication;
import cn.hserver.core.interfaces.PluginAdapter;
import cn.hserver.core.ioc.IocUtil;
import cn.hserver.core.ioc.ref.PackageScanner;
import cn.hserver.core.server.util.PropUtil;
import cn.hserver.plugin.gateway.business.Business;
import cn.hserver.plugin.gateway.config.GateWayConfig;
import cn.hserver.plugin.gateway.enums.GatewayMode;
import io.netty.channel.ChannelOption;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
