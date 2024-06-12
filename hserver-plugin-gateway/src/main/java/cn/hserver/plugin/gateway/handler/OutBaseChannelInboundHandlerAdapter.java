package cn.hserver.plugin.gateway.handler;

import cn.hserver.core.server.util.ReleaseUtil;
import cn.hserver.plugin.gateway.business.Business;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class OutBaseChannelInboundHandlerAdapter extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(OutBaseChannelInboundHandlerAdapter.class);
    private final Channel inboundChannel;
    private final Business business;
    public OutBaseChannelInboundHandlerAdapter(Channel inboundChannel, Business business) {
        this.inboundChannel = inboundChannel;
        this.business = business;
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        try {
            Object out = business.out(inboundChannel, msg);
            if (out == null) {
                ReleaseUtil.release(msg);
                return;
            }
            inboundChannel.writeAndFlush(out).addListener((ChannelFutureListener) future -> {
                if (!future.isSuccess()) {
                    ReleaseUtil.release(out);
                    future.channel().close();
                }
            });
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            ctx.channel().close();
            ReleaseUtil.release(msg);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        inboundChannel.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        business.exceptionCaught(ctx,cause);
        ctx.channel()
                .close();
    }
}
