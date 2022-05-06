package top.hserver.core.server.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.multipart.*;
import io.netty.handler.codec.http2.HttpConversionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hserver.HServerApplication;
import top.hserver.core.server.context.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import top.hserver.core.server.util.ByteBufUtil;
import top.hserver.core.server.util.HServerIpUtil;
import top.hserver.core.server.util.RequestIdGen;

import java.util.List;
import java.util.Map;

import static top.hserver.core.server.context.ConstConfig.SERVER_NAME;


/**
 * @author hxm
 */
public class HServerContentHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final Logger log = LoggerFactory.getLogger(HServerContentHandler.class);

    private final static DefaultHttpDataFactory FACTORY = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE);

    private final boolean isHttp2;

    public HServerContentHandler(boolean isHttp2) {
        this.isHttp2 = isHttp2;
    }

    public Channel outboundChannel;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest req) throws Exception {
        HServerContext hServerContext = new HServerContext();
        Request request = new Request();
        hServerContext.setRequest(request);
        if (isHttp2) {
            String streamId = req.headers().get(HttpConversionUtil.ExtensionHeaderNames.STREAM_ID.text());
            request.setStreamId(streamId);
        }
        request.setHandler(this);
        String id = RequestIdGen.getId();
        request.setRequestId(id);
        request.setIp(HServerIpUtil.getClientIp(channelHandlerContext));
        request.setPort(HServerIpUtil.getClientPort(channelHandlerContext));
        request.setCtx(channelHandlerContext);
        request.setNettyUri(req.uri());
        request.setNettyRequest(new DefaultFullHttpRequest(req.protocolVersion(), req.method(), req.uri(), Unpooled.copiedBuffer(req.content()), req.headers(), req.trailingHeaders()));
        handlerUrl(request, req);
        handlerBody(request, req);
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
        webkit.httpResponse.setHeader(ConstConfig.REQUEST_ID,id);
        webkit.httpResponse.setHeader(SERVER_NAME, ConstConfig.VERSION);
        hServerContext.setWebkit(webkit);
        HServerContextHolder.setWebKit(webkit);
        channelHandlerContext.fireChannelRead(hServerContext);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        BuildResponse.writeException(ctx, cause);
    }

    private void handlerUrl(Request request, FullHttpRequest req) {
        try {
            Map<String, List<String>> requestParams = request.getRequestParams();
            QueryStringDecoder decoder = new QueryStringDecoder(req.uri());
            Map<String, List<String>> params = decoder.parameters();
            for (Map.Entry<String, List<String>> next : params.entrySet()) {
                requestParams.put(next.getKey(), next.getValue());
                for (String s : next.getValue()) {
                    request.addReqUrlParams(next.getKey(), s);
                }
            }
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }

    private void handlerBody(Request request, FullHttpRequest req) {
        ByteBuf body = req.content().duplicate();
        request.setBody(ByteBufUtil.byteBufToBytes(body));
        try {
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(FACTORY, req);
            List<InterfaceHttpData> bodyHttpDates = decoder.getBodyHttpDatas();
            bodyHttpDates.forEach(request::writeHttpData);
            decoder.destroy();
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }

}