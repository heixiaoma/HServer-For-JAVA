package cn.hserver.plugin.gateway.protocol;

import cn.hserver.core.interfaces.ProtocolDispatcherAdapter;
import cn.hserver.core.ioc.annotation.Bean;
import cn.hserver.core.server.util.protocol.SSLUtils;
import cn.hserver.plugin.gateway.config.GateWayConfig;
import cn.hserver.plugin.gateway.enums.GatewayMode;
import cn.hserver.plugin.gateway.handler.http4.Http4FrontendHandler;
import cn.hserver.plugin.gateway.handler.tcp.FrontendHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * 网关模式
 */
@Bean
public class DispatchHttp4GateWay implements ProtocolDispatcherAdapter {
    @Override
    public boolean dispatcher(ChannelHandlerContext ctx, ChannelPipeline pipeline, byte[] headers) {
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().localAddress();
        //TCP模式
        if (GateWayConfig.GATEWAY_MODE == GatewayMode.HTTP_4 && socketAddress.getPort() == GateWayConfig.PORT) {
            int i = SSLUtils.verifyPacket(ByteBuffer.wrap(headers));
            pipeline.addLast(new Http4FrontendHandler());
        }
        return false;
    }
}
