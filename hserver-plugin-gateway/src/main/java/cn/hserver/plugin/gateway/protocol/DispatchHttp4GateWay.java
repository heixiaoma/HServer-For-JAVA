package cn.hserver.plugin.gateway.protocol;

import cn.hserver.core.interfaces.ProtocolDispatcherAdapter;
import cn.hserver.core.ioc.annotation.Bean;
import cn.hserver.core.server.context.ConstConfig;
import cn.hserver.core.server.util.protocol.HostUtil;
import cn.hserver.core.server.util.protocol.ProtocolUtil;
import cn.hserver.core.server.util.protocol.SSLUtils;
import cn.hserver.plugin.gateway.config.GateWayConfig;
import cn.hserver.plugin.gateway.enums.GatewayMode;
import cn.hserver.plugin.gateway.handler.http4.Http4FrontendHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * 网关模式
 */
@Bean
public class DispatchHttp4GateWay implements ProtocolDispatcherAdapter {
    private static final Logger log = LoggerFactory.getLogger(DispatchHttp4GateWay.class);

    @Override
    public boolean dispatcher(ChannelHandlerContext ctx, ChannelPipeline pipeline, byte[] headers) {
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().localAddress();
        //TCP模式
        if (GateWayConfig.GATEWAY_MODE == GatewayMode.HTTP_4 && GateWayConfig.PORT.contains(socketAddress.getPort())) {
            //解析入场host
            String host = HostUtil.getHost(ByteBuffer.wrap(headers));
            if (host != null) {
                pipeline.channel().config().setWriteBufferHighWaterMark(GateWayConfig.HM*1024*1024);
                pipeline.channel().config().setWriteBufferLowWaterMark(GateWayConfig.LM*1024*1024);
                pipeline.addLast(new Http4FrontendHandler(host));
                return true;
            } else {
                log.error("不是标准http数据包");
                ProtocolUtil.print(ctx, DispatchHttp4GateWay.class.getName(), headers);
                return false;
            }
        }
        return false;
    }
}
