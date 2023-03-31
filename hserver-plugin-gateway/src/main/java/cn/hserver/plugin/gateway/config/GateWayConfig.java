package cn.hserver.plugin.gateway.config;

import cn.hserver.core.server.util.EventLoopUtil;
import cn.hserver.plugin.gateway.enums.GatewayMode;
import io.netty.channel.EventLoopGroup;

import java.util.ArrayList;
import java.util.List;

public class GateWayConfig {

    public static final EventLoopGroup EVENT_EXECUTORS = EventLoopUtil.getEventLoop(50, "GateWay");

    /**
     * 是否是HTTP网关
     */
    public static GatewayMode GATEWAY_MODE = GatewayMode.HTTP_7;

    /**
     * 网关占用端口
     */
    public static List<Integer> PORT = new ArrayList<Integer>(){
        {
            add(8888);
        }
    };

}
