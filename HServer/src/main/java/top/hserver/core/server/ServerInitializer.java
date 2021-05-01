package top.hserver.core.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.OptionalSslHandler;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import top.hserver.cloud.common.Msg;
import top.hserver.cloud.common.codec.RpcDecoder;
import top.hserver.cloud.common.codec.RpcEncoder;
import top.hserver.cloud.server.handler.RpcServerHandler;
import top.hserver.core.server.context.ConstConfig;
import top.hserver.core.server.handlers.HServerContentHandler;
import top.hserver.core.server.handlers.RouterHandler;
import top.hserver.core.server.handlers.WebSocketServerHandler;

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


    static class ProtocolDispatcher extends ByteToMessageDecoder {

        @Override
        public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            if (in.readableBytes() < 5) {
                return;
            }
            int readerIndex = in.readerIndex();
            final int magic1 = in.getByte(readerIndex);
            final int magic2 = in.getByte(readerIndex + 1);
            if (isHttp(magic1, magic2)) {
                dispatchHttp(ctx);
            } else {
                dispatchRpc(ctx);
            }
        }

        private boolean isHttp(int magic1, int magic2) {
            return
                    magic1 == 'G' && magic2 == 'E' || // GET
                            magic1 == 'P' && magic2 == 'O' || // POST
                            magic1 == 'P' && magic2 == 'U' || // PUT
                            magic1 == 'H' && magic2 == 'E' || // HEAD
                            magic1 == 'O' && magic2 == 'P' || // OPTIONS
                            magic1 == 'P' && magic2 == 'A' || // PATCH
                            magic1 == 'D' && magic2 == 'E' || // DELETE
                            magic1 == 'T' && magic2 == 'R' || // TRACE
                            magic1 == 'C' && magic2 == 'O';   // CONNECT
        }

        private void dispatchHttp(ChannelHandlerContext ctx) {
            ChannelPipeline pipeline = ctx.pipeline();

            if (ConstConfig.sslContext != null) {
                pipeline.addLast(new OptionalSslHandler(ConstConfig.sslContext));
            }

            if (ConstConfig.WRITE_LIMIT!=null&&ConstConfig.READ_LIMIT!=null) {
                pipeline.addLast(new GlobalTrafficShapingHandler(ctx.executor().parent(), ConstConfig.WRITE_LIMIT, ConstConfig.READ_LIMIT));
            }
            pipeline.addLast(new HttpServerCodec());
            pipeline.addLast(new HttpObjectAggregator(ConstConfig.HTTP_CONTENT_SIZE));
            //有websocket才走他
            if (WebSocketServerHandler.WebSocketRouter.size() > 0) {
                pipeline.addLast(ConstConfig.BUSINESS_EVENT, new WebSocketServerHandler());
            }
            pipeline.addLast(new HServerContentHandler());
            pipeline.addLast(ConstConfig.BUSINESS_EVENT, new RouterHandler());
            pipeline.remove(this);
            ctx.fireChannelActive();
        }

        private void dispatchRpc(ChannelHandlerContext ctx) {
            ChannelPipeline pipeline = ctx.pipeline();
            pipeline.addLast(new RpcDecoder(Msg.class));
            pipeline.addLast(new RpcEncoder(Msg.class));
            pipeline.addLast(ConstConfig.BUSINESS_EVENT, "RpcServerProviderHandler", new RpcServerHandler());
            pipeline.remove(this);
            ctx.fireChannelActive();
        }
    }
}
