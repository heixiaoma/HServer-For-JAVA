package top.hserver.cloud.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import top.hserver.cloud.common.Msg;
import top.hserver.cloud.common.codec.RpcDecoder;
import top.hserver.cloud.common.codec.RpcEncoder;

public class RpcServerInitializer extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch){
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new RpcDecoder(Msg.class));
        pipeline.addLast(new RpcEncoder(Msg.class));
        pipeline.addLast("ServerHandler", new ServerHandler());
    }

}
