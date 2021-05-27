package top.hserver.core.server.dispatcher;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.OptionalSslHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import top.hserver.cloud.common.Msg;
import top.hserver.cloud.common.codec.RpcDecoder;
import top.hserver.cloud.common.codec.RpcEncoder;
import top.hserver.cloud.server.handler.RpcServerHandler;
import top.hserver.core.interfaces.ProtocolDispatcherAdapter;
import top.hserver.core.ioc.annotation.Bean;
import top.hserver.core.ioc.annotation.Order;
import top.hserver.core.server.ServerInitializer;
import top.hserver.core.server.context.ConstConfig;
import top.hserver.core.server.handlers.HServerContentHandler;
import top.hserver.core.server.handlers.RouterHandler;
import top.hserver.core.server.handlers.WebSocketServerHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author hxm
 */
@Order(3)
@Bean
public class DispatchHttp implements ProtocolDispatcherAdapter {

    @Override
    public boolean dispatcher(ChannelHandlerContext ctx, ChannelPipeline pipeline, byte[] headers, ServerInitializer.ProtocolDispatcher protocolDispatcher) {
        if (isHttp(headers[0], headers[1])) {
            if (ConstConfig.sslContext != null) {
                pipeline.addLast(new OptionalSslHandler(ConstConfig.sslContext));
            }

            if (ConstConfig.WRITE_LIMIT != null && ConstConfig.READ_LIMIT != null) {
                pipeline.addLast(new GlobalTrafficShapingHandler(ctx.executor().parent(), ConstConfig.WRITE_LIMIT, ConstConfig.READ_LIMIT));
            }
            pipeline.addLast(new HttpServerCodec());
            pipeline.addLast(new HttpObjectAggregator(ConstConfig.HTTP_CONTENT_SIZE));
            //有websocket才走他
            if (WebSocketServerHandler.WebSocketRouter.size() > 0) {
                pipeline.addLast(ConstConfig.BUSINESS_EVENT, new WebSocketServerHandler());
            }
            if (ConstConfig.DEFAULT_STALE_CONNECTION_TIMEOUT != null) {
                pipeline.addLast(new IdleStateHandler(0, ConstConfig.DEFAULT_STALE_CONNECTION_TIMEOUT, 0, TimeUnit.MILLISECONDS));
            }
            pipeline.addLast(new HServerContentHandler());
            pipeline.addLast(ConstConfig.BUSINESS_EVENT, new RouterHandler());
            pipeline.remove(protocolDispatcher);
            ctx.fireChannelActive();
            return true;
        }
        return false;
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
}
