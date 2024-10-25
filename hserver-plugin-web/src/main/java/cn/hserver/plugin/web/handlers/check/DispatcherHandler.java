package cn.hserver.plugin.web.handlers.check;

import cn.hserver.core.ioc.IocUtil;
import cn.hserver.core.server.util.ExceptionUtil;
import cn.hserver.plugin.web.context.*;
import cn.hserver.plugin.web.exception.BusinessException;
import cn.hserver.plugin.web.handlers.BuildResponse;
import cn.hserver.plugin.web.interfaces.GlobalException;
import cn.hserver.plugin.web.interfaces.ResponseAdapter;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerKeepAliveHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface DispatcherHandler {
    Logger log = LoggerFactory.getLogger(DispatcherHandler.class);
    List<ResponseAdapter> responseAdapters = IocUtil.getListBean(ResponseAdapter.class);
    List<GlobalException> listBean = IocUtil.getListBean(GlobalException.class);

    HServerContext dispatcher(HServerContext context);

    /**
     * 构建返回对象
     *
     * @param hServerContext
     * @return
     */
    static FullHttpResponse buildResponse(HServerContext hServerContext) {
        try {
            FullHttpResponse response = null;
            if (hServerContext.getResponse().isUseCtx()) {
                return null;
            }
            /**
             * 如果是文件特殊处理下,是静态文件，同时需要下载的文件
             */
            else if (hServerContext.isStaticFile()) {
                //显示型的静态文件
                response = BuildResponse.buildStaticShowType(hServerContext);
            } else if (hServerContext.getResponse().isDownload()) {
                //控制器下载文件的
                response = BuildResponse.buildControllerDownloadType(hServerContext.getResponse());
            } else if (hServerContext.getResponse().getResult() != null) {
                String tempResult = hServerContext.getResponse().getResult();
                if (responseAdapters != null) {
                    for (ResponseAdapter responseAdapter : responseAdapters) {
                        tempResult = responseAdapter.result(hServerContext.getResponse().getResult());
                    }
                }
                hServerContext.getResponse().setResult(tempResult);
                //是否Response的
                response = BuildResponse.buildHttpResponseData(hServerContext.getResponse());
            } else {
                //使用了代理模式，让用户自由控制ctx 不受框架操作
                if (hServerContext.getResponse().isUseCtx()) {
                    return null;
                }
            }
            return BuildResponse.buildEnd(hServerContext.getRequest(), response, hServerContext.getResponse());
        } catch (Exception e) {
            throw new BusinessException(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), "构建Response对象异常", e, hServerContext.getWebkit());
        }
    }


    /**
     * 异常Resp
     *
     * @param webkit
     * @return
     */
    static FullHttpResponse buildExceptionResponse(Webkit webkit) {
        FullHttpResponse response = null;
        Response httpResponse = (Response) webkit.httpResponse;
        if (httpResponse.isDownload()) {
            //控制器下载文件的
            response = BuildResponse.buildControllerDownloadType(httpResponse);
        } else if (httpResponse.getResult() != null) {
            String tempResult = httpResponse.getResult();
            if (responseAdapters != null) {
                for (ResponseAdapter responseAdapter : responseAdapters) {
                    tempResult = responseAdapter.result(httpResponse.getResult());
                }
            }
            httpResponse.setResult(tempResult);
            //是否Response的
            response = BuildResponse.buildHttpResponseData(httpResponse);
        }
        if (response == null) {
            response = BuildResponse.buildString("你开启了全局异常处理，但是你没有处理.");
        }
        return BuildResponse.buildEnd((Request) webkit.httpRequest, response, httpResponse);
    }

    /**
     * 构建返回对象
     *
     * @param e
     * @return
     */
    static FullHttpResponse handleException(Throwable e) {
        try {
            //一般是走自己的异常
            if (e instanceof BusinessException) {
                BusinessException e1 = (BusinessException) e;
                if (listBean != null) {
                    for (GlobalException globalException : listBean) {
                        globalException.handler(e1.getThrowable(), e1.getHttpCode(), e1.getErrorDescription(), e1.getWebkit());
                        if (e1.getWebkit().httpResponse.hasData()) {
                            break;
                        }
                    }
                    return buildExceptionResponse(e1.getWebkit());
                } else {
                    if (e1.getHttpCode() == HttpResponseStatus.NOT_FOUND.code()) {
                        log.error(e1.getErrorDescription());
                    } else {
                        log.error(e1.getThrowable().getMessage(), e1.getThrowable());
                    }
                    return BuildResponse.buildError(e1);
                }
            } else {
                log.error(e.getMessage(), e);
                return BuildResponse.buildError(e);
            }
        } catch (Exception e2) {
            return BuildResponse.buildError(e2);
        }
    }

    /**
     * 终极输出
     *
     * @param ctx
     * @param hServerContext
     * @param msg
     */
    static void writeResponse(ChannelHandlerContext ctx, HServerContext hServerContext, FullHttpResponse msg) {
        //等于null 是让用户自己调度
        if (msg != null) {
            if (responseAdapters != null) {
                for (ResponseAdapter responseAdapter : responseAdapters) {
                    msg = responseAdapter.response(msg);
                }
            }
            if (log.isDebugEnabled()) {
                Request request = hServerContext.getRequest();
                log.debug("地址：{} 方法：{} 耗时：{}/ns 来源:{}", request.getNettyUri(), request.getRequestType().name(), ((System.nanoTime() - request.getCreateTime())), request.getIpAddress());
            }
            ctx.write(msg, ctx.voidPromise());
        }
    }

}
