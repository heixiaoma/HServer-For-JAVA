package cn.hserver.plugin.gateway.handler.http4;

import cn.hserver.plugin.gateway.business.BusinessHttp4;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http4BackendHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(Http4BackendHandler.class);

    private final Channel inboundChannel;

    private final BusinessHttp4 businessHttp4;
    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        log.debug("限制操作，让两个通道实现同步读写 开关状态:{}",ctx.channel().isWritable());
        inboundChannel.config().setAutoRead(ctx.channel().isWritable());
        super.channelWritabilityChanged(ctx);
    }
    public Http4BackendHandler(Channel inboundChannel, BusinessHttp4 businessHttp4) {
        this.inboundChannel = inboundChannel;
        this.businessHttp4 = businessHttp4;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.read();
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        try {
            Object out = businessHttp4.out(inboundChannel, msg);
            if (out == null) {
                return;
            }
            inboundChannel.writeAndFlush(out).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    ctx.channel().read();
                } else {
                    ReferenceCountUtil.release(out);
                    future.channel().close();
                }
            });
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            ctx.channel().close();
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Http4FrontendHandler.closeOnFlush(inboundChannel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Http4FrontendHandler.closeOnFlush(ctx.channel());
    }
}
