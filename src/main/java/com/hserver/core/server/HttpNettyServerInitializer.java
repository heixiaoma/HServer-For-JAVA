package com.hserver.core.server;

import com.hserver.core.server.handlers.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;


/**
 * Class for initialization ChannelPipeline
 * Created by Bess on 23.09.14.
 */
public class HttpNettyServerInitializer extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
//        pipeline.addLast(new HttpServerCodec());
//        pipeline.addLast(new HttpServerExpectContinueHandler());
//        if (false) {
//            pipeline.addLast(new HttpContentCompressor());
//        }
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpServerExpectContinueHandler());

        pipeline.addLast("对象合并",new ObjectHandler());
        pipeline.addLast("业务处理", new ActionHandler());
    }
}
