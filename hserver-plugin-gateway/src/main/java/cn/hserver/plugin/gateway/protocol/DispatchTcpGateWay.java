package cn.hserver.plugin.gateway.protocol;

import cn.hserver.core.interfaces.ProtocolDispatcherAdapter;
import cn.hserver.core.ioc.annotation.Bean;
import cn.hserver.plugin.gateway.config.GateWayConfig;
import cn.hserver.plugin.gateway.enums.GatewayMode;
import cn.hserver.plugin.gateway.handler.tcp.FrontendHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;

import java.net.InetSocketAddress;

/**
 * 网关模式
 */
@Bean
public class DispatchTcpGateWay implements ProtocolDispatcherAdapter {
    @Override
    public boolean dispatcher(ChannelHandlerContext ctx, ChannelPipeline pipeline, byte[] headers) {
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().localAddress();
        //TCP模式
        if (GateWayConfig.GATEWAY_MODE == GatewayMode.TCP && socketAddress.getPort() == GateWayConfig.PORT) {
            pipeline.addLast(new FrontendHandler());
        }
        return false;
    }
}
