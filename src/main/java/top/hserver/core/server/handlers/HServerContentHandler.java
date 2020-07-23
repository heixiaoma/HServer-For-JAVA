package top.hserver.core.server.handlers;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.multipart.*;
import top.hserver.core.server.context.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;
import top.hserver.core.server.util.HServerIpUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author hxm
 */
@Slf4j
public class HServerContentHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final static DefaultHttpDataFactory FACTORY = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest req) throws Exception {
        HServerContext hServerContext = new HServerContext();
        hServerContext.setFullHttpRequest(req);
        Request request = new Request();
        hServerContext.setRequest(request);
        request.setIp(HServerIpUtil.getClientIp(channelHandlerContext));
        request.setPort(HServerIpUtil.getClientPort(channelHandlerContext));
        request.setCtx(channelHandlerContext);
        request.setNettyUri(req.uri());
        DefaultFullHttpRequest defaultFullHttpRequest;
        hServerContext.setCtx(channelHandlerContext);
        if (req.method() == HttpMethod.GET) {
            Map<String, String> requestParams = new HashMap<>(1);
            QueryStringDecoder decoder = new QueryStringDecoder(req.uri());
            Map<String, List<String>> params = decoder.parameters();
            for (Map.Entry<String, List<String>> next : params.entrySet()) {
                requestParams.put(next.getKey(), next.getValue().get(0));
            }
            request.setRequestParams(requestParams);
            defaultFullHttpRequest= new DefaultFullHttpRequest(req.protocolVersion(),req.method(),req.uri(), Unpooled.buffer(0), req.headers(),req.trailingHeaders());
        } else {
            defaultFullHttpRequest= new DefaultFullHttpRequest(req.protocolVersion(),req.method(),req.uri(),Unpooled.copiedBuffer(req.content()), req.headers(),req.trailingHeaders());
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(FACTORY, req);
            List<InterfaceHttpData> bodyHttpDates = decoder.getBodyHttpDatas();
            bodyHttpDates.forEach(request::writeHttpData);
            decoder.destroy();
            byte[] b = new byte[req.content().readableBytes()];
            req.content().readBytes(b);
            request.setBody(b);
        }
        request.setNettyRequest(defaultFullHttpRequest);
        //获取URi，設置真實的URI
        int i = req.uri().indexOf("?");
        if (i > 0) {
            String uri = req.uri();
            request.setUri(uri.substring(0, i));
        } else {
            request.setUri(req.uri());
        }
        request.setRequestType(req.method());
        //处理Headers
        HeadMap headers = new HeadMap();
        req.headers().names().forEach(a -> headers.put(a, req.headers().get(a)));
        request.setHeaders(headers);
        hServerContext.setRequest(request);
        hServerContext.setResponse(new Response());
        Webkit webkit = new Webkit();
        webkit.httpRequest = hServerContext.getRequest();
        webkit.httpResponse = hServerContext.getResponse();
        hServerContext.setWebkit(webkit);

        channelHandlerContext.fireChannelRead(hServerContext);
    }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    BuildResponse.writeException(ctx,cause);
  }
}