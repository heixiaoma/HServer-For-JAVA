package cn.hserver.plugin.gateway.protocol;

import cn.hserver.core.interfaces.ProtocolDispatcherAdapter;
import cn.hserver.core.interfaces.ProtocolDispatcherSuperAdapter;
import cn.hserver.core.ioc.annotation.Bean;
import cn.hserver.core.ioc.annotation.Order;
import cn.hserver.plugin.gateway.config.GateWayConfig;
import cn.hserver.plugin.gateway.enums.GatewayMode;
import cn.hserver.plugin.gateway.handler.http7.Http7FrontendHandler;
import cn.hserver.plugin.gateway.handler.http7.Http7ObjectAggregator;
import cn.hserver.plugin.gateway.handler.http7.Http7WebSocketFrontendHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

import java.net.InetSocketAddress;

/**
 * 网关模式
 */
@Bean
@Order(1)
public class DispatchHttp7GateWay implements ProtocolDispatcherSuperAdapter {

    @Override
    public boolean dispatcher(Channel channel, ChannelPipeline pipeline) {
        InetSocketAddress socketAddress = (InetSocketAddress) channel.localAddress();
        if (GateWayConfig.GATEWAY_MODE == GatewayMode.HTTP_7 && GateWayConfig.PORT.contains(socketAddress.getPort())) {
            Http7FrontendHandler http7FrontendHandler = new Http7FrontendHandler();
            pipeline.addLast(new HttpServerCodec(), new Http7ObjectAggregator(Integer.MAX_VALUE,http7FrontendHandler.getBusinessHttp7().ignoreUrls()));
            pipeline.addLast(new Http7WebSocketFrontendHandler());
            pipeline.addLast(http7FrontendHandler);
            return true;
        }
        return false;
    }
}
