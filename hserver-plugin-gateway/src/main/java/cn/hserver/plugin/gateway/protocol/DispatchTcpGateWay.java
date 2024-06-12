package cn.hserver.plugin.gateway.protocol;

import cn.hserver.core.interfaces.ProtocolDispatcherAdapter;
import cn.hserver.core.interfaces.ProtocolDispatcherSuperAdapter;
import cn.hserver.core.ioc.IocUtil;
import cn.hserver.core.ioc.annotation.Bean;
import cn.hserver.core.ioc.annotation.Order;
import cn.hserver.plugin.gateway.business.Business;
import cn.hserver.plugin.gateway.business.BusinessHttp4;
import cn.hserver.plugin.gateway.business.BusinessHttp7;
import cn.hserver.plugin.gateway.business.BusinessTcp;
import cn.hserver.plugin.gateway.config.GateWayConfig;
import cn.hserver.plugin.gateway.enums.GatewayMode;
import cn.hserver.plugin.gateway.handler.http7.Http7FrontendHandler;
import cn.hserver.plugin.gateway.handler.http7.Http7WebSocketFrontendHandler;
import cn.hserver.plugin.gateway.handler.http7.aggregator.Http7RequestObjectAggregator;
import cn.hserver.plugin.gateway.handler.tcp.FrontendHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpServerCodec;

import java.net.InetSocketAddress;

/**
 * 网关模式
 */
@Bean
@Order(1)
public class DispatchTcpGateWay implements ProtocolDispatcherSuperAdapter {
    private volatile static Business business;

    @Override
    public boolean dispatcher(Channel channel, ChannelPipeline pipeline) {
        InetSocketAddress socketAddress = (InetSocketAddress) channel.localAddress();

        if (GateWayConfig.PORT.contains(socketAddress.getPort())){

            if (GateWayConfig.GATEWAY_MODE == GatewayMode.HTTP_7) {
                if (business == null) {
                    business = IocUtil.getSupperBean(BusinessHttp7.class);
                }

                Http7FrontendHandler http7FrontendHandler = new Http7FrontendHandler(business);
                pipeline.addLast(new HttpServerCodec(), new Http7RequestObjectAggregator(Integer.MAX_VALUE, channel, http7FrontendHandler.getRequestIgnoreUrls()));
                pipeline.addLast(new Http7WebSocketFrontendHandler(business));
                pipeline.addLast(http7FrontendHandler);
                return true;
            }
            //TCP模式
            if (GateWayConfig.GATEWAY_MODE == GatewayMode.TCP) {
                if (business == null) {
                    business = IocUtil.getSupperBean(BusinessTcp.class);
                }
                pipeline.addLast(new FrontendHandler(business));
                return true;
            }

        }
        return false;
    }
}
