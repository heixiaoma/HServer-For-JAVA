package net.hserver.plugin.rpc.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import net.hserver.plugin.rpc.codec.Msg;
import net.hserver.plugin.rpc.codec.RpcDecoder;
import net.hserver.plugin.rpc.codec.RpcEncoder;

public class ClientHandlersInitializer extends ChannelInitializer<SocketChannel> {

    private NettyChannel nettyChannel;

    public ClientHandlersInitializer(NettyChannel nettyChannel) {
        this.nettyChannel = nettyChannel;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new RpcDecoder(Msg.class));
        pipeline.addLast(new RpcEncoder(Msg.class));
        pipeline.addLast("idleStateHandler", new IdleStateHandler(5, 5, 3));
        pipeline.addLast("ClientHandler", new ClientHandler(nettyChannel));
    }
}