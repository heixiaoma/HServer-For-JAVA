package cn.hserver.plugin.web.protocol;

import cn.hserver.core.interfaces.ProtocolDispatcherAdapter;
import cn.hserver.core.ioc.annotation.Bean;
import cn.hserver.core.ioc.annotation.Order;
import cn.hserver.plugin.web.context.WebConstConfig;
import cn.hserver.plugin.web.handlers.*;
import cn.hserver.plugin.web.handlers.check.Filter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.OptionalSslHandler;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;

/**
 * @author hxm
 */
@Order(3)
@Bean
public class DispatchHttp implements ProtocolDispatcherAdapter {

    private static GlobalTrafficShapingHandler globalTrafficShapingHandler;

    @Override
    public boolean dispatcher(ChannelHandlerContext ctx, ChannelPipeline pipeline, byte[] headers) {
        //如果是http
        if (isHttp(headers[0], headers[1])) {
            httpHandler(ctx);
            return true;
        }

        //如果是https
        if (isHttps(headers[0])) {
            if (WebConstConfig.sslContext != null) {
                pipeline.addLast(new OptionalSslHandler(WebConstConfig.sslContext));
                httpHandler(ctx);
                return true;
            }
        }
        return false;
    }

    public static void httpHandler(ChannelHandlerContext ctx) {
        ChannelPipeline pipeline = ctx.pipeline();
        if (WebConstConfig.WRITE_LIMIT != null && WebConstConfig.READ_LIMIT != null) {
            if (globalTrafficShapingHandler == null) {
                globalTrafficShapingHandler = new GlobalTrafficShapingHandler(ctx.executor(), WebConstConfig.WRITE_LIMIT, WebConstConfig.READ_LIMIT);
            }
            pipeline.addLast(WebConstConfig.BUSINESS_EVENT, globalTrafficShapingHandler);
        }
        pipeline.addLast(WebConstConfig.BUSINESS_EVENT, new HttpServerCodec());
        pipeline.addLast(WebConstConfig.BUSINESS_EVENT, new HttpObjectAggregator(WebConstConfig.HTTP_CONTENT_SIZE));
        //有websocket才走他
        if (!WebSocketServerHandler.WEB_SOCKET_ROUTER.isEmpty()) {
            pipeline.addLast(WebConstConfig.BUSINESS_EVENT, new WebSocketServerHandler());
        }
        pipeline.addLast(WebConstConfig.BUSINESS_EVENT, HServerContentHandler.getInstance());
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

    private boolean isHttps(int magic1) {
        /**
         * https 交互按 client hello ->sever hello 开头协议表达
         * （22,3,1）转为16进制为 1603010
         */
        return magic1 == 22;
    }

}
