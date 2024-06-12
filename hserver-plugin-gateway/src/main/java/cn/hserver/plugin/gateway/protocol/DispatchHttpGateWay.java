package cn.hserver.plugin.gateway.protocol;

import cn.hserver.core.interfaces.ProtocolDispatcherAdapter;
import cn.hserver.core.interfaces.ProtocolDispatcherSuperAdapter;
import cn.hserver.core.ioc.IocUtil;
import cn.hserver.core.ioc.annotation.Bean;
import cn.hserver.core.ioc.annotation.Order;
import cn.hserver.core.server.util.protocol.HostUtil;
import cn.hserver.core.server.util.protocol.ProtocolUtil;
import cn.hserver.plugin.gateway.business.Business;
import cn.hserver.plugin.gateway.business.BusinessHttp4;
import cn.hserver.plugin.gateway.business.BusinessHttp7;
import cn.hserver.plugin.gateway.config.GateWayConfig;
import cn.hserver.plugin.gateway.enums.GatewayMode;
import cn.hserver.plugin.gateway.handler.http4.Http4FrontendHandler;
import cn.hserver.plugin.gateway.handler.http7.Http7FrontendHandler;
import cn.hserver.plugin.gateway.handler.http7.Http7WebSocketFrontendHandler;
import cn.hserver.plugin.gateway.handler.http7.aggregator.Http7RequestObjectAggregator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpServerCodec;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * 网关模式
 */
@Bean
@Order(1)
public class DispatchHttpGateWay implements ProtocolDispatcherAdapter {
    private volatile static Business business;

    @Override
    public boolean dispatcher(ChannelHandlerContext ctx, ChannelPipeline pipeline, byte[] headers) {
        Channel channel = ctx.channel();
        InetSocketAddress socketAddress = (InetSocketAddress) channel.localAddress();
        //TCP模式
        if (GateWayConfig.PORT.contains(socketAddress.getPort())) {

            if (GateWayConfig.GATEWAY_MODE == GatewayMode.HTTP_4) {

                if (business == null) {
                    business = IocUtil.getSupperBean(BusinessHttp4.class);
                }

                //解析入场host
                String host = HostUtil.getHost(ByteBuffer.wrap(headers));
                if (host != null) {
                    pipeline.addLast(new Http4FrontendHandler(host,business));
                    return true;
                } else {
                    ProtocolUtil.print(ctx, DispatchHttpGateWay.class.getName(), headers);
                    return false;
                }
            }

        }
        return false;
    }
}
