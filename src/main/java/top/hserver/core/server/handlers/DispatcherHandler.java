package top.hserver.core.server.handlers;

import com.alibaba.fastjson.JSON;
import top.hserver.core.interfaces.GlobalException;
import top.hserver.core.interfaces.PermissionAdapter;
import top.hserver.core.ioc.IocUtil;
import top.hserver.core.server.context.*;
import top.hserver.core.server.exception.BusinessException;
import top.hserver.core.server.filter.FilterChain;
import top.hserver.core.server.router.RouterInfo;
import top.hserver.core.server.router.RouterManager;
import top.hserver.core.server.router.RouterPermission;
import top.hserver.core.server.util.ExceptionUtil;
import top.hserver.core.server.util.ParameterUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 分发器
 *
 * @author hxm
 */
@Slf4j
public class DispatcherHandler {

    private final static StaticHandler STATIC_HANDLER = new StaticHandler();

    /**
     * 静态文件的处理
     *
     * @param hServerContext
     * @return
     */
    static HServerContext staticFile(HServerContext hServerContext) {
        StaticFile handler = STATIC_HANDLER.handler(hServerContext.getRequest().getUri(), hServerContext);
        if (handler != null) {
            hServerContext.setStaticFile(true);
            hServerContext.setStaticFile(handler);
        }
        return hServerContext;
    }

    /**
     * 权限验证
     *
     * @param hServerContext
     * @return
     */
    static HServerContext permission(HServerContext hServerContext) {

        //如果是静态文件就不进行权限验证了
        if (hServerContext.isStaticFile()) {
            return hServerContext;
        }
        PermissionAdapter permissionAdapter = IocUtil.getBean(PermissionAdapter.class);
        if (permissionAdapter != null) {
            RouterPermission routerPermission = RouterManager.getRouterPermission(hServerContext.getRequest().getUri(), hServerContext.getRequest().getRequestType());
            if (routerPermission != null) {
                if (routerPermission.getRequiresPermissions() != null) {
                    try {
                        permissionAdapter.requiresPermissions(routerPermission.getRequiresPermissions(), hServerContext.getWebkit());
                    } catch (Exception e) {
                        throw new BusinessException(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), "权限验证", e, hServerContext.getWebkit());
                    }
                }
                if (routerPermission.getRequiresRoles() != null) {
                    try {
                        permissionAdapter.requiresRoles(routerPermission.getRequiresRoles(), hServerContext.getWebkit());
                    } catch (Exception e) {
                        throw new BusinessException(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), "角色验证", e, hServerContext.getWebkit());
                    }
                }
                if (routerPermission.getSign() != null) {
                    try {
                        permissionAdapter.sign(routerPermission.getSign(), hServerContext.getWebkit());
                    } catch (Exception e) {
                        throw new BusinessException(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), "Sign验证", e, hServerContext.getWebkit());
                    }
                }
            }
        }
        return hServerContext;
    }

    /**
     * 拦截器
     *
     * @param hServerContext
     * @return
     */
    public static HServerContext filter(HServerContext hServerContext) {
        /**
         * 检测下Filter的过滤哈哈
         */
        if (!FilterChain.FILTERS_IOC.isEmpty()) {
            //不是空就要进行Filter过滤洛
            hServerContext.setFilter(true);
            try {
                FilterChain.getFileChain().doFilter(hServerContext.getWebkit());
            } catch (Exception e) {
                throw new BusinessException(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), "拦截器异常", e, hServerContext.getWebkit());
            }
            //Filter走完又回来
        }
        return hServerContext;
    }


    /**
     * 去执行控制器的方法
     *
     * @param hServerContext
     * @return
     */
    static HServerContext findController(HServerContext hServerContext) {

        /**
         * 如果静态文件就跳过当前的处理，否则就去执行控制器的方法
         */
        if (hServerContext.isStaticFile()) {
            return hServerContext;
        }

        /**
         * 检查下Filter是否有值了
         */
        if (hServerContext.getResponse().getJsonAndHtml() != null || hServerContext.getResponse().isDownload()) {
            return hServerContext;
        }

        RouterInfo routerInfo = RouterManager.getRouterInfo(hServerContext.getRequest().getUri(), hServerContext.getRequest().getRequestType(), hServerContext);
        if (routerInfo == null) {
            StringBuilder error = new StringBuilder();
            error.append("未找到对应的控制器，请求方式：")
                    .append(hServerContext.getRequest().getRequestType().toString())
                    .append("，请求路径：")
                    .append(hServerContext.getRequest().getUri());
            throw new BusinessException(HttpResponseStatus.NOT_FOUND.code(), error.toString(), new Exception("不能找到处理当前请求的资源"), hServerContext.getWebkit());
        }
        Method method = routerInfo.getMethod();
        Class<?> aClass = routerInfo.getAClass();
        Object bean = IocUtil.getBean(aClass);
        //检查下方法参数
        Object res;
        //如果控制器有参数，那么就进行，模糊赋值，在检测是否有req 和resp
        try {

            Object[] methodArgs = null;
            try {
                methodArgs = ParameterUtil.getMethodArgs(aClass, method, hServerContext);
            } catch (Exception e) {
                throw new BusinessException(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), "控制器方法调用时传入的参数异常", e, hServerContext.getWebkit());
            }
            res = method.invoke(bean, methodArgs);
            //调用结果进行设置
            if (res == null) {
                hServerContext.setResult("");
            } else if (String.class.getName().equals(res.getClass().getName())) {
                hServerContext.setResult(res.toString());
            } else {
                //非字符串类型的将对象转换Json字符串
                hServerContext.setResult(JSON.toJSONString(res));
                hServerContext.getResponse().setHeader("content-type", "application/json;charset=UTF-8");
            }
        } catch (InvocationTargetException e) {
            throw new BusinessException(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), "调用方法失败", e.getTargetException(), hServerContext.getWebkit());
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new BusinessException(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), "调用控制器时参数异常", e, hServerContext.getWebkit());
        }
        return hServerContext;
    }

    /**
     * 构建返回对象
     *
     * @param hServerContext
     * @return
     */
    static FullHttpResponse buildResponse(HServerContext hServerContext) {
        try {
            FullHttpResponse response;
            /**
             * 如果是文件特殊处理下,是静态文件，同时需要下载的文件
             */
            if (hServerContext.isStaticFile()) {
                //显示型的静态文件
                response = BuildResponse.buildStaticShowType(hServerContext);
            } else if (hServerContext.getResponse().isDownload()) {
                //控制器下载文件的
                response = BuildResponse.buildControllerDownloadType(hServerContext.getResponse());
            } else if (hServerContext.getResponse().getJsonAndHtml() != null) {
                //是否Response的
                response = BuildResponse.buildHttpResponseData(hServerContext.getResponse());
            } else {
                //控制器调用的
                response = BuildResponse.buildControllerResult(hServerContext);
            }
            return BuildResponse.buildEnd(response, hServerContext.getResponse());
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
    private static FullHttpResponse buildExceptionResponse(Webkit webkit) {
        FullHttpResponse response = null;
        Response httpResponse = (Response) webkit.httpResponse;
        if (httpResponse.isDownload()) {
            //控制器下载文件的
            response = BuildResponse.buildControllerDownloadType(httpResponse);
        } else if (httpResponse.getJsonAndHtml() != null) {
            //是否Response的
            response = BuildResponse.buildHttpResponseData(httpResponse);
        }
        if (response == null) {
            response = BuildResponse.buildString("你开启了全局异常处理，但是你没有处理.");
        }
        return BuildResponse.buildEnd(response, httpResponse);
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
            if (e.getCause() instanceof BusinessException) {
                BusinessException e1 = (BusinessException) e.getCause();
                if (e1.getHttpCode() == HttpResponseStatus.NOT_FOUND.code()) {
                    log.error(e1.getErrorDescription());
                } else {
                    log.error(ExceptionUtil.getMessage(e1.getThrowable()));
                }
                GlobalException bean2 = IocUtil.getBean(GlobalException.class);
                if (bean2 != null) {
                    bean2.handler(e1.getThrowable(), e1.getHttpCode(), e1.getErrorDescription(), e1.getWebkit());
                    return buildExceptionResponse(e1.getWebkit());
                } else {
                    return BuildResponse.buildError(e1);
                }
            } else {
                log.error(ExceptionUtil.getMessage(e));
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
     * @param future
     * @param msg
     */
    static void writeResponse(ChannelHandlerContext ctx, CompletableFuture<HServerContext> future, FullHttpResponse msg) {
        ctx.writeAndFlush(msg);
        future.complete(null);
    }

}
