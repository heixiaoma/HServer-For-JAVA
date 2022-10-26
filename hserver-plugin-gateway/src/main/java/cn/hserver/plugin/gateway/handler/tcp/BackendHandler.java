package cn.hserver.plugin.gateway.handler.tcp;

import cn.hserver.plugin.gateway.business.BusinessTcp;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class BackendHandler extends ChannelInboundHandlerAdapter {

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
        Object out = businessTcp.out(inboundChannel,msg);
        if (out==null){
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