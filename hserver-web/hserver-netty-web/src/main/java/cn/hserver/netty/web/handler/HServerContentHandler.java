package cn.hserver.netty.web.handler;

import cn.hserver.core.config.ConstConfig;
import cn.hserver.mvc.constants.WebConstConfig;
import cn.hserver.mvc.context.WebContext;
import cn.hserver.mvc.context.WebContextHolder;
import cn.hserver.mvc.pipeline.PipelineExecutor;
import cn.hserver.mvc.request.HeadMap;
import cn.hserver.mvc.util.RequestIdGen;
import cn.hserver.netty.web.context.HttpRequest;
import cn.hserver.netty.web.context.HttpResponse;
import cn.hserver.netty.web.handler.http.ResponseHandler;
import cn.hserver.netty.web.handler.http.UseCtxHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.multipart.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import java.util.List;
import java.util.Map;

/**
 * @author hxm
 */
@ChannelHandler.Sharable
public class HServerContentHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final HServerContentHandler instance = new HServerContentHandler();

    private HServerContentHandler() {
    }

    public static HServerContentHandler getInstance() {
        return instance;
    }

    private static final Logger log = LoggerFactory.getLogger(HServerContentHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        HttpRequest request = new HttpRequest();
        String id = RequestIdGen.getId();
        request.setRequestId(id);
        request.setCtx(ctx);
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
        //处理session
        if (WebConstConfig.SESSION_MANAGER != null) {
            request.setHttpSession(WebConstConfig.SESSION_MANAGER.createSession(request));
        }
        HttpResponse response = new HttpResponse();
        response.setHeader(WebConstConfig.REQUEST_ID, id);
        response.setHeader(WebConstConfig.SERVER_NAME, ConstConfig.VERSION);
        response.setHeader("Server", WebConstConfig.SERVER_NAME);
        response.setCtx(ctx);
        WebContext webContext = new WebContext(request, response);
        try {
            WebContextHolder.setWebContext(webContext);
            PipelineExecutor.executor(webContext);
            if (response.isUseCtx()){
                UseCtxHandler.useCtx(ctx,request,response);
            }else {
                ResponseHandler.writeResponse(ctx, webContext);
            }
        } catch (Throwable e) {
            ResponseHandler.writeException(ctx, e);
        }finally {
            WebContextHolder.remove();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ResponseHandler.writeException(ctx, cause);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }


    private void handlerUrl(HttpRequest request, FullHttpRequest req) {
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

    private void handlerBody(HttpRequest request, FullHttpRequest req) {
        try {
            ByteBuf body = req.content().duplicate();
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(req, -1, -1);
            List<InterfaceHttpData> bodyHttpDates = decoder.getBodyHttpDatas();
            InterfaceHttpData interfaceHttpData = bodyHttpDates.stream().filter(k -> k.getHttpDataType() == InterfaceHttpData.HttpDataType.FileUpload).findFirst().orElse(null);
            if (interfaceHttpData == null) {
                request.setBody(ByteBufUtil.getBytes(body));
            }
            bodyHttpDates.forEach(request::writeHttpData);
            decoder.destroy();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
    }

}
