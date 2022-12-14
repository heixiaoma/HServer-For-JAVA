package cn.hserver.plugin.gateway.protocol;

import cn.hserver.core.interfaces.ProtocolDispatcherAdapter;
import cn.hserver.core.ioc.annotation.Bean;
import cn.hserver.plugin.gateway.config.GateWayConfig;
import cn.hserver.plugin.gateway.enums.GatewayMode;
import cn.hserver.plugin.gateway.handler.http7.Http7FrontendHandler;
import cn.hserver.plugin.gateway.handler.http7.Http7WebSocketFrontendHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;

import java.net.InetSocketAddress;

/**
 * 网关模式
 */
@Bean
public class DispatchHttp7GateWay implements ProtocolDispatcherAdapter {
    @Override
    public boolean dispatcher(ChannelHandlerContext ctx, ChannelPipeline pipeline, byte[] headers) {
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().localAddress();
        if (GateWayConfig.GATEWAY_MODE == GatewayMode.HTTP_7 && GateWayConfig.PORT.contains(socketAddress.getPort())) {
            pipeline.addLast(new HttpServerCodec(), new HttpObjectAggregator(Integer.MAX_VALUE));
            pipeline.addLast(new Http7WebSocketFrontendHandler());
            pipeline.addLast(new Http7FrontendHandler());
            return true;
        }
        return false;
    }
}
