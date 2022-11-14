package cn.hserver.plugin.gateway.handler.http7;

import cn.hserver.plugin.gateway.business.BusinessHttp7;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpObject;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http7BackendHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(Http7BackendHandler.class);

    private final Channel inboundChannel;

    private final BusinessHttp7 businessHttp7;

    public Http7BackendHandler(Channel inboundChannel,BusinessHttp7 businessHttp7) {
        this.inboundChannel = inboundChannel;
        this.businessHttp7=businessHttp7;
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof HttpObject) {
            try {
                Object out = businessHttp7.out(inboundChannel, msg);
                if (out == null) {
                    return;
                }
                inboundChannel.writeAndFlush(out);
            }catch (Throwable e){
                log.error(e.getMessage(),e);
            }finally {
                ctx.channel().close();
                ReferenceCountUtil.release(msg);
            }
        } else {
            ctx.channel().close();
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Http7FrontendHandler.closeOnFlush(inboundChannel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Http7FrontendHandler.closeOnFlush(ctx.channel());
    }
}
