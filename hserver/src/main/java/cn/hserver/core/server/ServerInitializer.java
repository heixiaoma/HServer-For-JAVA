package cn.hserver.core.server;

import cn.hserver.core.interfaces.ProtocolDispatcherSuperAdapter;
import cn.hserver.core.server.context.ConstConfig;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import cn.hserver.core.interfaces.ProtocolDispatcherAdapter;
import cn.hserver.core.ioc.IocUtil;
import io.netty.util.ReferenceCountUtil;

import java.util.List;

/**
 * @author hxm
 */
public class ServerInitializer extends ChannelInitializer<Channel> {

    private final static List<ProtocolDispatcherSuperAdapter> listSuperBean = IocUtil.getListBean(ProtocolDispatcherSuperAdapter.class);
    private final static List<ProtocolDispatcherAdapter> listBean = IocUtil.getListBean(ProtocolDispatcherAdapter.class);

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (listSuperBean != null && !listSuperBean.isEmpty()) {
            for (ProtocolDispatcherSuperAdapter protocolDispatcherSuperAdapter : listSuperBean) {
                if (protocolDispatcherSuperAdapter.dispatcher(ch, pipeline)) {
                    return;
                }
            }
        }
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
            ByteBuf slice = in.slice(0, Math.min(in.readableBytes(), ConstConfig.PRE_PROTOCOL_MAX_SIZE));
            byte[] bytes = ByteBufUtil.getBytes(slice);
            ChannelPipeline pipeline = ctx.pipeline();
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

            /**
             * 协议无解对其关闭
             */
            ctx.close();
        }
    }

}
