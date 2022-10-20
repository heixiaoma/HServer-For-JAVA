package cn.hserver.plugin.gateway.handler.tcp;

import cn.hserver.core.ioc.IocUtil;
import cn.hserver.plugin.gateway.business.Business;
import cn.hserver.plugin.gateway.business.BusinessTcp;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ReferenceCountUtil;

public class FrontendHandler extends ChannelInboundHandlerAdapter {

    private Channel outboundChannel;
    private BusinessTcp businessTcp;

    public FrontendHandler() {
        for (Business business : IocUtil.getListBean(Business.class)) {
            if (business instanceof BusinessTcp) {
                this.businessTcp = (BusinessTcp)business;
            }
        }
    }

    static void closeOnFlush(Channel ch) {
        if (ch.isActive()) {
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws InterruptedException {
        final Channel inboundChannel = ctx.channel();
        Bootstrap b = new Bootstrap();
        b.group(inboundChannel.eventLoop());
        b.option(ChannelOption.AUTO_READ, true)
                .channel(NioSocketChannel.class)
                .handler(new BackendHandler(inboundChannel, businessTcp));
        ChannelFuture f = b.connect(businessTcp.getProxyHost(ctx, null, ctx.channel().remoteAddress())).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                inboundChannel.read();
            } else {
                inboundChannel.close();
            }
        });
        outboundChannel = f.channel();
        ctx.channel().config().setAutoRead(false);
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        Object in = businessTcp.in(ctx, msg);
        if (in == null) {
            return;
        }
        if (outboundChannel.isActive()) {
            outboundChannel.writeAndFlush(in).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    ctx.channel().read();
                } else {
                    ReferenceCountUtil.release(in);
                    future.channel().close();
                    //泄漏
                }
            });
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (outboundChannel != null) {
            closeOnFlush(outboundChannel);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        closeOnFlush(ctx.channel());
    }
}