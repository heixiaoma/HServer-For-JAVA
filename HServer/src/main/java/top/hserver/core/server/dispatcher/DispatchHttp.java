package top.hserver.core.server.dispatcher;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http2.DefaultHttp2Connection;
import io.netty.handler.codec.http2.HttpToHttp2ConnectionHandlerBuilder;
import io.netty.handler.codec.http2.InboundHttp2ToHttpAdapter;
import io.netty.handler.codec.http2.InboundHttp2ToHttpAdapterBuilder;
import io.netty.handler.ssl.OptionalSslHandler;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import top.hserver.core.interfaces.ProtocolDispatcherAdapter;
import top.hserver.core.ioc.annotation.Bean;
import top.hserver.core.ioc.annotation.Order;
import top.hserver.core.server.context.ConstConfig;
import top.hserver.core.server.handlers.HServerContentHandler;
import top.hserver.core.server.handlers.Http2OrHttpHandler;
import top.hserver.core.server.handlers.RouterHandler;
import top.hserver.core.server.handlers.WebSocketServerHandler;

/**
 * @author hxm
 */
@Order(3)
@Bean
public class DispatchHttp implements ProtocolDispatcherAdapter {

    @Override
    public boolean dispatcher(ChannelHandlerContext ctx, ChannelPipeline pipeline, byte[] headers) {
        //如果是https
        if (isHttps(headers[0], headers[1], headers[2])) {
            if (ConstConfig.sslContext != null) {
                pipeline.addLast(ConstConfig.sslContext.newHandler(ctx.alloc()));
                if (ConstConfig.openHttp2) {
                    //分发协商处理是否支持http2 不支持就 用http1.1
                    pipeline.addLast(new Http2OrHttpHandler());
                } else {
                    httpHandler(ctx);
                }
            }
            return true;
        }
        //如果是http
        if (isHttp(headers[0], headers[1])) {
            httpHandler(ctx);
            return true;
        }
        return false;
    }

    public static void httpHandler(ChannelHandlerContext ctx) {
        ChannelPipeline pipeline = ctx.pipeline();
        if (ConstConfig.WRITE_LIMIT != null && ConstConfig.READ_LIMIT != null) {
            pipeline.addLast(ConstConfig.BUSINESS_EVENT, new GlobalTrafficShapingHandler(ctx.executor().parent(), ConstConfig.WRITE_LIMIT, ConstConfig.READ_LIMIT));
        }
        pipeline.addLast(ConstConfig.BUSINESS_EVENT, new HttpServerCodec());
        pipeline.addLast(ConstConfig.BUSINESS_EVENT, new HttpObjectAggregator(ConstConfig.HTTP_CONTENT_SIZE));
        //有websocket才走他
        if (WebSocketServerHandler.WEB_SOCKET_ROUTER.size() > 0) {
            pipeline.addLast(ConstConfig.BUSINESS_EVENT, new WebSocketServerHandler());
        }
        pipeline.addLast(ConstConfig.BUSINESS_EVENT, new HServerContentHandler(false));
        pipeline.addLast(ConstConfig.BUSINESS_EVENT, new RouterHandler());
    }

    public static void http2Handler(ChannelHandlerContext ctx) {
        DefaultHttp2Connection connection = new DefaultHttp2Connection(true);
        InboundHttp2ToHttpAdapter listener = new InboundHttp2ToHttpAdapterBuilder(connection)
                .propagateSettings(true).validateHttpHeaders(false)
                .maxContentLength(ConstConfig.HTTP_CONTENT_SIZE).build();

        ctx.pipeline().addLast(new HttpToHttp2ConnectionHandlerBuilder()
                .frameListener(listener)
                .connection(connection).build());

        if (ConstConfig.WRITE_LIMIT != null && ConstConfig.READ_LIMIT != null) {
            ctx.pipeline().addLast(ConstConfig.BUSINESS_EVENT, new GlobalTrafficShapingHandler(ctx.executor().parent(), ConstConfig.WRITE_LIMIT, ConstConfig.READ_LIMIT));
        }
        ctx.pipeline().addLast(ConstConfig.BUSINESS_EVENT, new HServerContentHandler(true));
        ctx.pipeline().addLast(ConstConfig.BUSINESS_EVENT, new RouterHandler());
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

    private boolean isHttps(int magic1, int magic2, int magic3) {
        /**
         * https 交互按 client hello ->sever hello 开头协议表达
         * （22,3,1）转为16进制为 1603010
         */
        return magic1 == 22 && magic2 == 3 && magic3 == 1;
    }

}
