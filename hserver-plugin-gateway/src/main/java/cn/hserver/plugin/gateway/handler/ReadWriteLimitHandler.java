package cn.hserver.plugin.gateway.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadWriteLimitHandler extends ChannelDuplexHandler {
    private static final Logger log = LoggerFactory.getLogger(ReadWriteLimitHandler.class);

    private Channel remoteChannel;
    public ReadWriteLimitHandler() {
    }

    public ReadWriteLimitHandler( Channel remoteChannel, Channel channel) {
        this.remoteChannel = remoteChannel;
        //未对方通道也设置一个限制
        if (channel != null) {
            remoteChannel.pipeline().addFirst(new ReadWriteLimitHandler( channel, null));
        }
    }

    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {
        readWriteLimit(ctx.channel());
        super.read(ctx);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        readWriteLimit(ctx.channel());
        super.write(ctx, msg, promise);
    }



    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        readWriteLimit(ctx.channel());
        super.channelWritabilityChanged(ctx);
    }

    /**
     * 读写限制
     *
     * @param channel
     */
    public void readWriteLimit(Channel channel) {
        if (remoteChannel != null) {
            if (channel.isWritable()&&!remoteChannel.config().isAutoRead()) {
                remoteChannel.config().setAutoRead(true);
            } else if (!channel.isWritable()&&remoteChannel.config().isAutoRead()) {
                remoteChannel.config().setAutoRead(false);
            }
        } else {
            if (channel.isWritable()) {
                channel.config().setAutoRead(true);
            } else if (!channel.isWritable()) {
                channel.config().setAutoRead(false);
            }
        }
    }
}
