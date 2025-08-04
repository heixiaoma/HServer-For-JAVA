package cn.hserver.netty.web.handler.http;

import cn.hserver.core.config.ConstConfig;
import cn.hserver.core.util.ExceptionUtil;
import cn.hserver.mvc.constants.MimeType;
import cn.hserver.mvc.constants.WebConstConfig;
import cn.hserver.mvc.context.WebContext;
import cn.hserver.mvc.request.Request;
import cn.hserver.netty.web.context.DefaultCookie;
import cn.hserver.netty.web.context.HttpRequest;
import cn.hserver.netty.web.context.HttpResponse;
import cn.hserver.netty.web.util.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static io.netty.handler.codec.http.HttpResponseStatus.FOUND;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;


public class ResponseHandler {

    private static final Logger log = LoggerFactory.getLogger(ResponseHandler.class);


    private static void writeException(ChannelHandlerContext ctx, Throwable cause, HttpResponseStatus status) {
        String message = ExceptionUtil.getMessage(cause);
        message = WebConstConfig.SERVER_NAME + ":" + ConstConfig.VERSION + "服务器异常:\n" + message;

        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                status,
                Unpooled.wrappedBuffer(message.getBytes(StandardCharsets.UTF_8)));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=UTF-8");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }


    /**
     * 响应文件类型
     *
     * @param response1
     * @return
     */
    private static FullHttpResponse buildFileType(HttpResponse response1) {
        FullHttpResponse response;
        if (response1.getResponseFile().getInputStream() != null) {
            InputStream inputStream = response1.getResponseFile().getInputStream();
            //getInputStream类型
            response = new DefaultFullHttpResponse(
                    HTTP_1_1,
                    HttpResponseStatus.OK,
                    Unpooled.wrappedBuffer(Objects.requireNonNull(ByteBufUtil.fileToByteBuf(inputStream))));
            try {
                inputStream.close();
            } catch (IOException ignored) {
            }
        } else {
            //File类型
            response = new DefaultFullHttpResponse(
                    HTTP_1_1,
                    HttpResponseStatus.OK,
                    Unpooled.wrappedBuffer(Objects.requireNonNull(ByteBufUtil.fileToByteBuf(response1.getResponseFile().getFile()))));
        }

        response.headers().set(HttpHeaderNames.CONTENT_TYPE, MimeType.getFileType(response1.getResponseFile().getFileName()) + ";charset=UTF-8");
        //attachment下载模式，inline预览模式
        String fileName = response1.getResponseFile().getFileName();
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8");
        } catch (Exception e) {
            log.warn("URL:{} 编码失败:{}", fileName, e.getMessage());
        }
        response.headers().add(HttpHeaderNames.CONTENT_DISPOSITION, String.format("inline; filename=\"%s\"", fileName));
        return response;
    }


    /**
     * 响应文本类型
     *
     * @return
     */
    private static FullHttpResponse buildHttpResponseData(String resStr) {
        return new DefaultFullHttpResponse(
                HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.wrappedBuffer(resStr.getBytes(StandardCharsets.UTF_8)));
    }


    /**
     * 对于head头相关的数据设置，做下收尾
     *
     * @param response
     * @param response1
     * @return
     */
    private static FullHttpResponse handlerHead(HttpRequest request, FullHttpResponse response, HttpResponse response1) {
        if (response1.getHttpResponseStatus() != null) {
            response.setStatus(response1.getHttpResponseStatus());
        }
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);

        if (request.getInnerHttpSession() != null) {
            cn.hserver.netty.web.context.DefaultCookie defaultCookie = new DefaultCookie(WebConstConfig.SESSION_KEY, request.getInnerHttpSession().id());
            defaultCookie.setHttpOnly(true);
            response1.addCookie(defaultCookie);
        }
        Set<io.netty.handler.codec.http.cookie.Cookie> cookies = response1.getCookies();
        if (cookies != null) {
            List<String> encode = ServerCookieEncoder.LAX.encode(cookies);
            for (String s : encode) {
                response.headers().add(HttpHeaderNames.SET_COOKIE, s);
            }
        }
        //用户自定义头头
        Map<String, String> headers = response1.getHeaders();
        headers.forEach((a, b) -> {
            response.headers().set(a, b);
            if (a.equalsIgnoreCase("location")) {
                response.setStatus(FOUND);
            }
        });

        return response;
    }


    /**
     * 构建返回对象
     *
     * @param webContext
     * @return
     */
    private static FullHttpResponse buildResponse(WebContext webContext) throws Throwable {
        HttpResponse httpResponse = (HttpResponse) webContext.response;
        FullHttpResponse response = null;
        if (httpResponse.isUseCtx()) {
            return null;
        }
        if (httpResponse.isFile()) {
            //控制器下载文件的
            response = buildFileType(httpResponse);
        } else if (httpResponse.getResult() != null) {
            //是否Response的
            response = buildHttpResponseData(httpResponse.getResult());
        }
        if (response != null) {
            return handlerHead((HttpRequest) webContext.request, response, httpResponse);
        }
        return null;
    }


    public static void writeException(ChannelHandlerContext ctx, Throwable cause) {
        writeException(ctx, cause, HttpResponseStatus.SERVICE_UNAVAILABLE);
    }


    /**
     * 响应数据
     *
     * @param ctx
     * @param webContext
     */
    public static void writeResponse(ChannelHandlerContext ctx, WebContext webContext) {
        try {
            FullHttpResponse fullHttpResponse = buildResponse(webContext);
            if (fullHttpResponse != null) {
                ctx.write(fullHttpResponse);
            }
        } catch (Throwable e) {
            writeException(ctx, e);
        }
        if (log.isDebugEnabled()) {
            Request request = webContext.request;
            log.debug("地址：{} 方法：{} 耗时：{}/ms 来源:{}", request.getUriWithParams(), request.getRequestMethod().name(), ((System.currentTimeMillis() - request.getCreateTime())), request.getIpAddress());
        }
    }

}
