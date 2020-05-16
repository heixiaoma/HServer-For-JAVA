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
import top.hserver.core.server.util.ParameterUtil;
import top.hserver.core.server.util.ExceptionUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.*;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import static io.netty.handler.codec.http.HttpMethod.GET;
/**
 * 分发器
 */
@Slf4j
public class DispatcherHandler {

  private final static StatisticsHandler statisticsHandler = new StatisticsHandler();
  private final static StaticHandler staticHandler = new StaticHandler();
  private static final HttpDataFactory HTTP_DATA_FACTORY = new DefaultHttpDataFactory(true);
  //标识不是静态文件，这样下次使用方便直接跳过检查
  private final static CopyOnWriteArraySet<String> noStaticFileUri = new CopyOnWriteArraySet<>();

  static WebContext buildWebContext(ChannelHandlerContext ctx,
                                    WebContext webContext) {
    HttpRequest req = webContext.getHttpRequest();
    Request request = new Request();
    request.setIp(statisticsHandler.getClientIp(ctx));
    request.setPort(statisticsHandler.getClientPort(ctx));
    request.setCtx(ctx);
    request.setNettyUri(req.uri());
    webContext.setCtx(ctx);
    //如果GET请求
    if (req.method() == GET) {
      Map<String, String> requestParams = new HashMap<>();
      QueryStringDecoder decoder = new QueryStringDecoder(req.uri());
      Map<String, List<String>> parame = decoder.parameters();
      for (Map.Entry<String, List<String>> next : parame.entrySet()) {
        requestParams.put(next.getKey(), next.getValue().get(0));
      }
      request.setRequestParams(requestParams);
    } else {
      //檢查是否有文件上传
      try {
        HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(HTTP_DATA_FACTORY, req);
        boolean isMultipart = decoder.isMultipart();
        List<ByteBuf> byteBuffs = new ArrayList<>(webContext.getContents().size());
        for (HttpContent content : webContext.getContents()) {
          if (!isMultipart) {
            byteBuffs.add(content.content().copy());
          }
          decoder.offer(content);
          request.readHttpDataChunkByChunk(decoder);
          content.release();
        }
        if (!byteBuffs.isEmpty()) {
          request.setBody(Unpooled.copiedBuffer(byteBuffs.toArray(new ByteBuf[0])));
        }
      } catch (Exception e) {
        GlobalException bean2 = IocUtil.getBean(GlobalException.class);
        if (bean2 != null) {
          bean2.handler(e, webContext.getWebkit());
        } else {
          String message = ExceptionUtil.getMessage(e);
          log.error(message);
          throw new BusinessException(503, "生成解码器失败" + message);
        }
      }
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
    webContext.setRequest(request);
    webContext.setResponse(new Response());
    Webkit webkit = new Webkit();
    webkit.httpRequest = webContext.getRequest();
    webkit.httpResponse = webContext.getResponse();
    webContext.setWebkit(webkit);
    return webContext;
  }

  /**
   * 统计
   *
   * @param webContext
   * @return
   */
  static WebContext Statistics(WebContext webContext) {
    if (ConstConfig.isStatisticsOpen) {
      for (String statisticalRule : ConstConfig.StatisticalRules) {
        if (webContext.getRequest().getUri().matches(statisticalRule)) {
          long startTime = System.currentTimeMillis();
          //统计方法调用时长
          webContext.regStatistics((stopTime) -> {
            //URI访问数
            statisticsHandler.uriDataCount(webContext.getRequest().getUri());
            //请求总数统计
            statisticsHandler.increaseCount();
            //統計IP
            statisticsHandler.addToIpMap(webContext.getCtx());
            //计算统计请求的，数据包大小,IP,执行时间
            statisticsHandler.addToConnectionDeque(webContext.getCtx(), webContext.getRequest().getUri(), stopTime - startTime);
          });
          break;
        }
      }
    }
    return webContext;
  }


  /**
   * 静态文件的处理
   *
   * @param webContext
   * @return
   */
  static WebContext staticFile(WebContext webContext) {
    //检查是不是静态文件，如果是封装下请求，然后跳过控制器的方法
    if (noStaticFileUri.contains(webContext.getRequest().getUri())|| webContext.getRequest().getRequestType() != HttpMethod.GET) {
      return webContext;
    }
    StaticFile handler = staticHandler.handler(webContext.getRequest().getUri(), webContext);
    if (handler != null) {
      webContext.setStaticFile(true);
      webContext.setStaticFile(handler);
    } else {
      noStaticFileUri.add(webContext.getRequest().getUri());
    }
    return webContext;
  }


  /**
   * 权限验证
   *
   * @param webContext
   * @return
   */
  static WebContext Permission(WebContext webContext) {

    //如果是静态文件就不进行权限验证了
    if (webContext.isStaticFile()) {
      return webContext;
    }
    PermissionAdapter permissionAdapter = IocUtil.getBean(PermissionAdapter.class);
    if (permissionAdapter != null) {
      RouterPermission routerPermission = RouterManager.getRouterPermission(webContext.getRequest().getUri(), webContext.getRequest().getRequestType());
      if (routerPermission != null) {
        if (routerPermission.getRequiresPermissions() != null) {
          try {
            permissionAdapter.requiresPermissions(routerPermission.getRequiresPermissions(), webContext.getWebkit());
          } catch (Exception e) {
            GlobalException bean2 = IocUtil.getBean(GlobalException.class);
            if (bean2 != null) {
              bean2.handler(e, webContext.getWebkit());
              return webContext;
            } else {
              String message = ExceptionUtil.getMessage(e);
              log.error(message);
              throw new BusinessException(503, "权限验证" + message);
            }
          }
        }
        if (routerPermission.getRequiresRoles() != null) {
          try {
            permissionAdapter.requiresRoles(routerPermission.getRequiresRoles(), webContext.getWebkit());
          } catch (Exception e) {
            GlobalException bean2 = IocUtil.getBean(GlobalException.class);
            if (bean2 != null) {
              bean2.handler(e, webContext.getWebkit());
              return webContext;
            } else {
              String message = ExceptionUtil.getMessage(e);
              log.error(message);
              throw new BusinessException(503, "角色验证" + message);
            }
          }
        }
        if (routerPermission.getSign() != null) {
          try {
            permissionAdapter.sign(routerPermission.getSign(), webContext.getWebkit());
          } catch (Exception e) {
            GlobalException bean2 = IocUtil.getBean(GlobalException.class);
            if (bean2 != null) {
              bean2.handler(e, webContext.getWebkit());
              return webContext;
            } else {
              String message = ExceptionUtil.getMessage(e);
              log.error(message);
              throw new BusinessException(503, "Sign验证" + message);
            }
          }
        }
      }
    }
    return webContext;
  }

  /**
   * 拦截器
   *
   * @param webContext
   * @return
   */
  public static WebContext filter(WebContext webContext) {
    /**
     * 检测下Filter的过滤哈哈
     */
    if (!FilterChain.filtersIoc.isEmpty()) {
      //不是空就要进行Filter过滤洛
      webContext.setFilter(true);

      try {
        FilterChain.getFileChain().doFilter(webContext.getWebkit());
      } catch (Exception e) {
        GlobalException bean2 = IocUtil.getBean(GlobalException.class);
        if (bean2 != null) {
          bean2.handler(e, webContext.getWebkit());
          return webContext;
        } else {
          String message = ExceptionUtil.getMessage(e);
          log.error(message);
          throw new BusinessException(503, "拦截器异常" + message);
        }
      }
      //Filter走完又回来
    }
    return webContext;
  }


  /**
   * 去执行控制器的方法
   *
   * @param webContext
   * @return
   */
  static WebContext findController(WebContext webContext) {

    /**
     * 如果静态文件就跳过当前的处理，否则就去执行控制器的方法
     */
    if (webContext.isStaticFile()) {
      return webContext;
    }

    /**
     * 检查下Filter是否有值了
     */
    if (webContext.getResponse().getJsonAndHtml() != null || webContext.getResponse().isDownload()) {
      return webContext;
    }

    RouterInfo routerInfo = RouterManager.getRouterInfo(webContext.getRequest().getUri(), webContext.getRequest().getRequestType(), webContext);
    if (routerInfo == null) {
      GlobalException bean1 = IocUtil.getBean(GlobalException.class);
      if (bean1 != null) {
        bean1.handler(new NullPointerException("未找到对应的控制器，请求方式："+webContext.getRequest().getRequestType().toString()), webContext.getWebkit());
        return webContext;
      } else {
        log.error("未找到对应的控制器，请求方式："+webContext.getRequest().getRequestType().toString());
        throw new BusinessException(404, "未找到对应的控制器，请求方式："+webContext.getRequest().getRequestType().toString());
      }
    }
    try {
      Method method = routerInfo.getMethod();
      Class<?> aClass = routerInfo.getAClass();
      Object bean = IocUtil.getBean(aClass);
      //检查下方法参数
      Object res;
      //如果控制器有参数，那么就进行，模糊赋值，在检测是否有req 和resp
      try {

        Object[] methodArgs = null;
        try {
          methodArgs = ParameterUtil.getMethodArgs(aClass, method, webContext);
        } catch (Exception e) {
          GlobalException bean2 = IocUtil.getBean(GlobalException.class);
          if (bean2 != null) {
            bean2.handler(e, webContext.getWebkit());
            return webContext;
          } else {
            String message = ExceptionUtil.getMessage(e);
            log.error(message);
            throw new BusinessException(503, "生成控制器时参数异常" + message);
          }
        }
        res = method.invoke(bean, methodArgs);
        //调用结果进行设置
        if (res == null) {
          webContext.setResult("");
        } else if (res.getClass().getName().equals("java.lang.String")) {
          webContext.setResult(res.toString());
        } else {
          //非字符串类型的将对象转换Json字符串
          webContext.setResult(JSON.toJSONString(res));
          webContext.getResponse().setHeader("content-type", "application/json;charset=UTF-8");
        }
      } catch (IllegalAccessException | InvocationTargetException e) {
        GlobalException bean2 = IocUtil.getBean(GlobalException.class);
        if (bean2 != null) {
          bean2.handler(e, webContext.getWebkit());
          return webContext;
        } else {
          String message = ExceptionUtil.getMessage(e);
          log.error(message);
          throw new BusinessException(503, "调用方法失败" + message);
        }
      } catch (IllegalArgumentException e) {
        GlobalException bean1 = IocUtil.getBean(GlobalException.class);
        if (bean1 != null) {
          bean1.handler(e, webContext.getWebkit());
          return webContext;
        } else {
          String message = ExceptionUtil.getMessage(e);
          log.error(message);
          throw new BusinessException(503, "调用控制器时参数异常" + message);
        }
      }
      return webContext;
    } catch (BusinessException e) {
      GlobalException bean1 = IocUtil.getBean(GlobalException.class);
      if (bean1 != null) {
        bean1.handler(e, webContext.getWebkit());
      } else {
        String message = ExceptionUtil.getMessage(e);
        log.error(message);
        throw new BusinessException(e.getHttpCode(), e.getMsg() + message);
      }
    }
    return webContext;
  }

  /**
   * 构建返回对象
   *
   * @param webContext
   * @return
   */
  static FullHttpResponse buildResponse(WebContext webContext) {
    try {
      FullHttpResponse response;
      /**
       * 如果是文件特殊处理下,是静态文件，同时需要下载的文件
       */
      if (webContext.isStaticFile()) {
        //显示型的静态文件
        response = BuildResponse.buildStaticShowType(webContext);
      } else if (webContext.getResponse().isDownload()) {
        //控制器下载文件的
        response = BuildResponse.buildControllerDownloadType(webContext);
      } else if (webContext.getResponse().getJsonAndHtml() != null) {
        //是否Response的
        response = BuildResponse.buildHttpResponseData(webContext);
      } else {
        //控制器调用的
        response = BuildResponse.buildControllerResult(webContext);
      }
      return BuildResponse.buildEnd(response, webContext);
    } catch (Exception e) {
      String message = ExceptionUtil.getMessage(e);
      log.error(message);
      throw new BusinessException(503, "构建Response对象异常" + message);
    }
  }

  /**
   * 构建返回对象
   *
   * @param e
   * @return
   */
  static FullHttpResponse handleException(Throwable e) {
    return BuildResponse.buildError(e);
  }

  /**
   * 终极输出
   *
   * @param ctx
   * @param future
   * @param msg
   */
  static void writeResponse(ChannelHandlerContext
                              ctx, CompletableFuture<WebContext> future, FullHttpResponse msg) {
    ctx.writeAndFlush(msg);
    future.complete(null);
  }

}
