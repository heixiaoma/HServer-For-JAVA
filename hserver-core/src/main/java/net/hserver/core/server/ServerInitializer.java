package net.hserver.core.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.hserver.core.interfaces.ProtocolDispatcherAdapter;
import net.hserver.core.ioc.IocUtil;
import net.hserver.core.server.util.ByteBufUtil;

import java.util.List;

/**
 * @author hxm
 */
public class ServerInitializer extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new ProtocolDispatcher());
    }

    public static class ProtocolDispatcher extends ByteToMessageDecoder {
        @Override
        public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            if (in.readableBytes() < 5) {
                return;
            }
            /**
             * copy 最多512个字节作为消息头数据判断
             */
            ByteBuf slice = in.slice(0, Math.min(in.readableBytes(), 512));
            byte[] bytes = ByteBufUtil.byteBufToBytes(slice);
            ChannelPipeline pipeline = ctx.pipeline();
            List<ProtocolDispatcherAdapter> listBean = IocUtil.getListBean(ProtocolDispatcherAdapter.class);
            if (listBean == null) {
                return;
            }
            for (ProtocolDispatcherAdapter protocolDispatcherAdapter : listBean) {
                if (protocolDispatcherAdapter.dispatcher(ctx, pipeline, bytes)) {
                    ctx.pipeline().remove(this);
                    ctx.fireChannelActive();
                    return;
                }
            }
        }
    }

}
