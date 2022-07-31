package cn.hserver.plugin.gateway.config;

import cn.hserver.plugin.gateway.enums.GatewayMode;

public class GateWayConfig {

    /**
     * 是否是HTTP网关
     */
    public static GatewayMode GATEWAY_MODE = GatewayMode.HTTP;

    /**
     * 网关占用端口
     */
    public static Integer PORT = 8888;

}
