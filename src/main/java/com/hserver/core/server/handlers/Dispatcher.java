package com.hserver.core.server.handlers;

import com.alibaba.fastjson.JSON;
import com.hserver.core.ioc.IocUtil;
import com.hserver.core.server.context.Request;
import com.hserver.core.server.context.WebContext;
import com.hserver.core.server.exception.BusinessException;
import com.hserver.core.server.router.RequestType;
import com.hserver.core.server.router.RouterInfo;
import com.hserver.core.server.router.RouterManager;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
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
        webContext.setRequest(new Request());
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
     * 静态文件的处理
     *
     * @param webContext
     * @return
     */
    public static WebContext staticFile(WebContext webContext) {
        //检查是不是静态文件，如果是封装下请求，然后跳过控制器的方法
        webContext.setStaticFile(true);
        return webContext;
    }

    /**
     * 去执行控制器的方法
     *
     * @param webContext
     * @return
     */
    public static WebContext findController(WebContext webContext) {

        /**
         * 如果静态文件就跳过当前的处理，否则就去执行控制器的方法
         */
        if (webContext.isStaticFile()) {
            return webContext;
        }
        try {
            RouterInfo routerInfo = RouterManager.getRouterInfo("/hello", RequestType.GET);
            if (routerInfo == null) {
                throw new BusinessException(404, "为找到对应的解析器");
            }
            Method method = routerInfo.getMethod();
            Class<?> aClass = routerInfo.getaClass();
            Object bean = IocUtil.getBean(aClass);
            //检查下方法参数
            Type[] parameterTypes = method.getGenericParameterTypes();
            Object res;
            if (parameterTypes.length > 0) {
                Object[] objects = new Object[parameterTypes.length];
                for (int i = 0; i < parameterTypes.length; i++) {
                    //构建方法参数
                    if (parameterTypes[i].getTypeName().contains("com.hserver.core.server.context.Request")) {
                        objects[i] = webContext.getRequest();
                    } else if (parameterTypes[i].getTypeName().contains("com.hserver.core.server.context.Response")) {
                        objects[i] = webContext.getResponse();
                    } else {
                        objects[i] = null;
                    }
                }
                res = method.invoke(bean, objects);
            } else {
                res = method.invoke(bean);
            }
            if (res == null) {
                webContext.setResult("");
            } else if (res.getClass().getName().equals("java.lang.String")) {
                webContext.setResult(res.toString());
            } else {
                //转换Json输入
                webContext.setResult(JSON.toJSONString(res));
            }
            return webContext;
        } catch (Exception e) {
            throw new BusinessException(503, "调用方法失败" + e.getMessage());
        }

    }

    /**
     * 构建返回对象
     *
     * @param webContext
     * @return
     */
    public static FullHttpResponse buildResponse(WebContext webContext) {

        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.wrappedBuffer(webContext.getResult().getBytes(Charset.forName("UTF-8"))));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=UTF-8");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);

        return response;
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
