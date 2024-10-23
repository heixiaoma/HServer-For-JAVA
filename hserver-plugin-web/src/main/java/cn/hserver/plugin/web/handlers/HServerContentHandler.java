package cn.hserver.plugin.web.handlers;

import cn.hserver.core.server.context.ConstConfig;
import cn.hserver.plugin.web.context.*;
import cn.hserver.plugin.web.interfaces.HttpRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.multipart.*;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import cn.hserver.core.server.util.HServerIpUtil;
import cn.hserver.plugin.web.util.RequestIdGen;

import java.util.List;
import java.util.Map;

/**
 * @author hxm
 */
public class HServerContentHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final Logger log = LoggerFactory.getLogger(HServerContentHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest req) throws Exception {
        HServerContext hServerContext = new HServerContext();
        Request request = new Request();
        hServerContext.setRequest(request);
        String id = RequestIdGen.getId();
        request.setRequestId(id);
        request.setCtx(channelHandlerContext);
        request.setNettyUri(req.uri());
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
        webkit.httpResponse.setHeader(WebConstConfig.REQUEST_ID, id);
        webkit.httpResponse.setHeader(WebConstConfig.SERVER_NAME, ConstConfig.VERSION);
        webkit.httpResponse.setHeader("Server", WebConstConfig.SERVER_NAME);
        hServerContext.setWebkit(webkit);
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
        try {
            ByteBuf body = req.content().duplicate();
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder( req, -1,-1);
            List<InterfaceHttpData> bodyHttpDates = decoder.getBodyHttpDatas();
            InterfaceHttpData interfaceHttpData = bodyHttpDates.stream().filter(k -> k.getHttpDataType() == InterfaceHttpData.HttpDataType.FileUpload).findFirst().orElse(null);
            if (interfaceHttpData == null) {
                request.setBody(ByteBufUtil.getBytes(body));
            }
            bodyHttpDates.forEach(request::writeHttpData);
            decoder.destroy();
        } catch (Exception e) {
            log.warn(e.getMessage(),e);
        }
    }

}
