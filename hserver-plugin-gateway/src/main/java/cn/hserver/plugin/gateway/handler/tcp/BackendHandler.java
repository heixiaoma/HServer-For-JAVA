package cn.hserver.plugin.gateway.handler.tcp;

import cn.hserver.core.server.util.ReleaseUtil;
import cn.hserver.plugin.gateway.business.BusinessTcp;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackendHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(BackendHandler.class);

    private final Channel inboundChannel;
    private final BusinessTcp businessTcp;

    public BackendHandler(Channel inboundChannel, BusinessTcp businessTcp) {
        this.inboundChannel = inboundChannel;
        this.businessTcp = businessTcp;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.read();
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        try {
            Object out = businessTcp.out(inboundChannel,msg);
            if (out==null){
                ReleaseUtil.release(msg);
                return;
            }
            inboundChannel.writeAndFlush(out).addListener((ChannelFutureListener) future -> {
                if (!future.isSuccess()) {
                    ReleaseUtil.release(out);
                    future.channel().close();
                }
            });
        }catch (Throwable e){
            log.error(e.getMessage(),e);
            ctx.channel().close();
            ReleaseUtil.release(msg);

        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        FrontendHandler.closeOnFlush(inboundChannel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        FrontendHandler.closeOnFlush(ctx.channel());
    }
}
