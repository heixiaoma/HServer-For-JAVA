package com.hserver.core.server;

import com.hserver.core.server.handlers.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;


public class HttpNettyServerInitializer extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpServerExpectContinueHandler());
        //有socket才走他
        if (WebSocketServerHandler.WebSocketRouter.size() > 0) {
            pipeline.addLast(new WebSocketServerHandler());
        }
        pipeline.addLast("对象合并", new ObjectHandler());
        pipeline.addLast("业务处理", new ActionHandler());
    }
}
