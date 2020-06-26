package top.hserver.core.server.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.multipart.*;
import io.netty.util.ReferenceCountUtil;
import top.hserver.core.server.context.HServerContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;
import top.hserver.core.server.context.Request;
import top.hserver.core.server.context.Response;
import top.hserver.core.server.context.Webkit;
import top.hserver.core.server.util.HServerIpUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.netty.handler.codec.http.HttpMethod.GET;

/**
 * @author hxm
 */
@Slf4j
public class HServerContentHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final static DefaultHttpDataFactory FACTORY =new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE);
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
        hServerContext.setCtx(channelHandlerContext);
        if (req.method()==HttpMethod.GET) {
            Map<String, String> requestParams = new HashMap<>();
            QueryStringDecoder decoder = new QueryStringDecoder(req.uri());
            Map<String, List<String>> parame = decoder.parameters();
            for (Map.Entry<String, List<String>> next : parame.entrySet()) {
                requestParams.put(next.getKey(), next.getValue().get(0));
            }
            request.setRequestParams(requestParams);
        }else {
            byte[] b = new byte[req.content().readableBytes()];
            req.content().readBytes(b);
            request.setBody(b);
            HttpPostRequestDecoder decoder=new HttpPostRequestDecoder(FACTORY ,req);
            List<InterfaceHttpData> bodyHttpDatas = decoder.getBodyHttpDatas();
            bodyHttpDatas.forEach(request::writeHttpData);
            decoder.destroy();
        }

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
        Map<String, String> headers = new ConcurrentHashMap<>();
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
}