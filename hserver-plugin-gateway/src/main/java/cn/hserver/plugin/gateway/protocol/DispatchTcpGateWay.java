package cn.hserver.plugin.gateway.protocol;

import cn.hserver.core.interfaces.ProtocolDispatcherAdapter;
import cn.hserver.core.interfaces.ProtocolDispatcherSuperAdapter;
import cn.hserver.core.ioc.annotation.Bean;
import cn.hserver.plugin.gateway.config.GateWayConfig;
import cn.hserver.plugin.gateway.enums.GatewayMode;
import cn.hserver.plugin.gateway.handler.tcp.FrontendHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;

import java.net.InetSocketAddress;

/**
 * 网关模式
 */
@Bean
public class DispatchTcpGateWay implements ProtocolDispatcherSuperAdapter {
    @Override
    public boolean dispatcher(Channel channel, ChannelPipeline pipeline) {
        InetSocketAddress socketAddress = (InetSocketAddress) channel.localAddress();
        //TCP模式
        if (GateWayConfig.GATEWAY_MODE == GatewayMode.TCP && GateWayConfig.PORT.contains(socketAddress.getPort())) {
            pipeline.addLast(new FrontendHandler());
            return true;
        }
        return false;
    }
}
