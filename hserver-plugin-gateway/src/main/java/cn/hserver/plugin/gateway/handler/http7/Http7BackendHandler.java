package cn.hserver.plugin.gateway.handler.http7;

import cn.hserver.plugin.gateway.business.BusinessHttp7;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpObject;

public class Http7BackendHandler extends ChannelInboundHandlerAdapter {

    private final Channel inboundChannel;

    private final BusinessHttp7 businessHttp7;

    public Http7BackendHandler(Channel inboundChannel,BusinessHttp7 businessHttp7) {
        this.inboundChannel = inboundChannel;
        this.businessHttp7=businessHttp7;
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof HttpObject) {
            Object out = businessHttp7.out(inboundChannel,msg);
            if (out==null){
                return;
            }
            inboundChannel.writeAndFlush(out);
        } else {
            ctx.channel().close();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Http7FrontendHandler.closeOnFlush(inboundChannel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        Http7FrontendHandler.closeOnFlush(ctx.channel());
    }
}
