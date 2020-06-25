package top.hserver.core.server;

import io.netty.handler.codec.http.*;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.ssl.OptionalSslHandler;
import top.hserver.core.server.context.ConstConfig;
import top.hserver.core.server.handlers.ActionHandler;
import top.hserver.core.server.handlers.ObjectHandler;
import top.hserver.core.server.handlers.WebSocketServerHandler;


/**
 * @author hxm
 */
public class HttpNettyServerInitializer extends ChannelInitializer<Channel> {


    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (ConstConfig.sslContext!=null){
          pipeline.addLast(new OptionalSslHandler(ConstConfig.sslContext));
        }
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
