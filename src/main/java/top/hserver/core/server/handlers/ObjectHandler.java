package top.hserver.core.server.handlers;

import top.hserver.core.server.context.HServerContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

/**
 * @author hxm
 */
@Slf4j
public class ObjectHandler extends SimpleChannelInboundHandler<HttpObject> {
    private HServerContext HServerContext;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpObject msg) throws Exception {
        if (msg instanceof io.netty.handler.codec.http.HttpRequest) {
            HServerContext = new HServerContext();
            HServerContext.setHttpRequest((io.netty.handler.codec.http.HttpRequest) msg);
            return;
        }
        if (null != HServerContext && msg instanceof HttpContent) {
            HServerContext.appendContent((HttpContent) msg);
        }
        if (msg instanceof LastHttpContent) {
            if (null != HServerContext) {
                channelHandlerContext.fireChannelRead(HServerContext);
            } else {
                channelHandlerContext.fireChannelRead(msg);
            }
        }
    }
}