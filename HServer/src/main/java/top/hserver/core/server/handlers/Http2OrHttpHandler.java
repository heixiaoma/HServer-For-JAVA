package top.hserver.core.server.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ssl.ApplicationProtocolNames;
import io.netty.handler.ssl.ApplicationProtocolNegotiationHandler;
import top.hserver.core.server.dispatcher.DispatchHttp;

public class Http2OrHttpHandler extends ApplicationProtocolNegotiationHandler {

    public Http2OrHttpHandler() {
        super(ApplicationProtocolNames.HTTP_1_1);
    }

    @Override
    protected void configurePipeline(ChannelHandlerContext ctx, String protocol) throws Exception {
        if (ApplicationProtocolNames.HTTP_2.equals(protocol)) {
            configureHttp2(ctx);
            return;
        }
        if (ApplicationProtocolNames.HTTP_1_1.equals(protocol)) {
            configureHttp1(ctx);
            return;
        }
        throw new IllegalStateException("unknown protocol: " + protocol);
    }

    private static void configureHttp2(ChannelHandlerContext ctx) {
        DispatchHttp.http2Handler(ctx);
    }

    private static void configureHttp1(ChannelHandlerContext ctx) throws Exception {
        DispatchHttp.httpHandler(ctx);
    }
}