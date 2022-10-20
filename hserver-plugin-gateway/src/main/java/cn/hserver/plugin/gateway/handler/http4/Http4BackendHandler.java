package cn.hserver.plugin.gateway.handler.http4;

import cn.hserver.core.ioc.IocUtil;
import cn.hserver.plugin.gateway.business.BusinessHttp4;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class  Http4BackendHandler extends ChannelInboundHandlerAdapter {

    private final Channel inboundChannel;

    private final BusinessHttp4 businessHttp4;

    public Http4BackendHandler(Channel inboundChannel,BusinessHttp4 businessHttp4) {
        this.inboundChannel = inboundChannel;
        this.businessHttp4= businessHttp4;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.read();
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        Object out = businessHttp4.out(inboundChannel,msg);
        if (out==null){
            return;
        }
        inboundChannel.writeAndFlush(out).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                ctx.channel().read();
            } else {
                future.channel().close();
                ReferenceCountUtil.release(out);
            }
        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Http4FrontendHandler.closeOnFlush(inboundChannel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        Http4FrontendHandler.closeOnFlush(ctx.channel());
    }
}
