package cn.hserver.plugin.gateway.config;

import cn.hserver.plugin.gateway.enums.GatewayMode;

import java.util.ArrayList;
import java.util.List;

public class GateWayConfig {

    /**
     * 是否是HTTP网关
     */
    public static GatewayMode GATEWAY_MODE = GatewayMode.HTTP_7;

    public static Integer HM = 10;
    public static Integer LM = 5;

    /**
     * 网关占用端口
     */
    public static List<Integer> PORT = new ArrayList<Integer>(){
        {
            add(8888);
        }
    };

}
