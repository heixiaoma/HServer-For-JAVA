package com.hserver.core.server.handlers;

import com.hserver.core.ioc.IocUtil;
import com.hserver.core.server.WebContext;
import com.hserver.core.server.exception.BusinessException;
import com.hserver.core.server.router.RequestType;
import com.hserver.core.server.router.RouterInfo;
import com.hserver.core.server.router.RouterManager;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;

/**
 * 分发器
 */
public class Dispatcher {

    public static WebContext buildWebContext(ChannelHandlerContext ctx,
                                             HttpRequest req) {
        String remoteAddress = ctx.channel().remoteAddress().toString();
        WebContext webContext = new WebContext();
        return webContext;
    }

    /**
     * 统计
     *
     * @param webContext
     * @return
     */
    public static WebContext Statistics(WebContext webContext) {
        return webContext;
    }

    /**
     * 去执行控制器的方法
     *
     * @param webContext
     * @return
     */
    public static WebContext findController(WebContext webContext) {
        return webContext;
    }

    /**
     * 构建返回对象
     *
     * @param webContext
     * @return
     */
    public static FullHttpResponse buildResponse(WebContext webContext) {
        RouterInfo routerInfo = RouterManager.getRouterInfo("/hello", RequestType.GET);
        if (routerInfo == null) {
            throw new BusinessException(404, "为找到对应的解析器");
        }
        Method method = routerInfo.getMethod();
        Class<?> aClass = routerInfo.getaClass();
        Object bean = IocUtil.getBean(aClass);
        try {
            Object invoke = method.invoke(bean);
            FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK,
                    Unpooled.wrappedBuffer(invoke.toString().getBytes(Charset.forName("UTF-8"))));
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=UTF-8");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            return response;
        } catch (Exception e) {
            throw new BusinessException(503, "调用方法失败" + e.getMessage());
        }
    }

    /**
     * 构建返回对象
     *
     * @param e
     * @return
     */
    public static FullHttpResponse handleException(Throwable e) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.wrappedBuffer("error".getBytes(Charset.forName("UTF-8"))));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=UTF-8");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        return response;

    }

    /**
     * 终极输出
     *
     * @param ctx
     * @param future
     * @param msg
     */
    public static void writeResponse(ChannelHandlerContext ctx, CompletableFuture<HttpRequest> future, FullHttpResponse msg) {
        ctx.writeAndFlush(msg);
        future.complete(null);
    }

}
