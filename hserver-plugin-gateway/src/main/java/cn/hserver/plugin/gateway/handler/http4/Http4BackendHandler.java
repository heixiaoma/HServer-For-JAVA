package cn.hserver.plugin.gateway.handler.http4;

import cn.hserver.core.server.util.ReleaseUtil;
import cn.hserver.plugin.gateway.business.Business;
import cn.hserver.plugin.gateway.business.BusinessHttp4;
import cn.hserver.plugin.gateway.handler.OutBaseChannelInboundHandlerAdapter;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http4BackendHandler extends OutBaseChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(Http4BackendHandler.class);

    public Http4BackendHandler(Channel inboundChannel, Business business) {
        super(inboundChannel, business);
    }
}
