package top.hserver.cloud.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import top.hserver.cloud.common.MarshallingCodeCFactory;

public class FileServerInitializer extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch){
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(MarshallingCodeCFactory.buildMarshallingDecoder());
        pipeline.addLast(MarshallingCodeCFactory.buildMarshallingEncoder());
        pipeline.addLast("ServerHandler", new ServerHandler());
    }

}
