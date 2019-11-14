package com.hserver.core.server.handlers;

import com.hserver.core.server.stat.StatisticsController;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;

import static io.netty.handler.codec.http.HttpHeaderNames.LOCATION;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;

/**
 * redirect requests which contain "redirect?url="
 * Created by Bess on 23.09.14.
 */
public class RedirectHandler extends SimpleChannelInboundHandler<HttpRequest> {
    private static StatisticsController controller = new StatisticsController();
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpRequest req) throws Exception {
        String requestHTTP = req.uri();
        if (requestHTTP.contains("redirect?url=")) { //check if contains required characters
            String [] requestArray = requestHTTP.split("/?url=");
            String urlToRedirect = requestArray[1];
            if (!urlToRedirect.startsWith("http")) {
                urlToRedirect = "http://" + urlToRedirect;
            }
            FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HTTP_1_1, FOUND);
            fullHttpResponse.headers().set(LOCATION, urlToRedirect);
            ctx.writeAndFlush(fullHttpResponse).addListener(ChannelFutureListener.CLOSE);
            String url = req.uri();
            controller.IncreaseCount();
            controller.addToIpMap(ctx);
            controller.addToConnectionDeque(ctx, url);
            StatisticsController.processRedirectRequest(urlToRedirect);
        } else {
            ctx.fireChannelRead(req);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
