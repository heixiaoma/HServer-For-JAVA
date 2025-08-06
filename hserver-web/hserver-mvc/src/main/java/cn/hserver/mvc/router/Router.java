package cn.hserver.mvc.router;

import cn.hserver.core.context.IocApplicationContext;
import cn.hserver.mvc.annotation.Controller;
import cn.hserver.mvc.annotation.router.*;
import cn.hserver.mvc.constants.HttpMethod;
import cn.hserver.mvc.constants.HttpResponseStatus;
import cn.hserver.mvc.context.WebContext;
import cn.hserver.mvc.exception.MethodNotSupportException;
import cn.hserver.mvc.exception.NotFoundException;
import cn.hserver.mvc.exception.WebException;
import cn.hserver.mvc.util.ParameterUtil;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 * 路由接口的默认实现
 */
public class Router {

    private final List<Route> routes = new ArrayList<>();

    public Router route(String path, Handler handler,HttpMethod[] methods) {
        Route.Type type = determineRouteType(path);
        routes.add(new Route(path, handler, type, methods));
        return this;
    }

    public Router all(String path, Handler handler) {
        Route.Type type = determineRouteType(path);
        routes.add(new Route(path, handler, type, HttpMethod.values()));
        return this;
    }

    public Router get(String path, Handler handler) {
        Route.Type type = determineRouteType(path);
        routes.add(new Route(path, handler, type, new HttpMethod[]{HttpMethod.GET}));
        return this;
    }

    public Router post(String path, Handler handler) {
        Route.Type type = determineRouteType(path);
        routes.add(new Route(path, handler, type, new HttpMethod[]{HttpMethod.POST}));
        return this;
    }

    public Router put(String path, Handler handler) {
        Route.Type type = determineRouteType(path);
        routes.add(new Route(path, handler, type, new HttpMethod[]{HttpMethod.PUT}));
        return this;
    }

    public Router delete(String path, Handler handler) {
        Route.Type type = determineRouteType(path);
        routes.add(new Route(path, handler, type, new HttpMethod[]{HttpMethod.DELETE}));
        return this;
    }


    public boolean matchAndHandle(WebContext ctx) throws Throwable {
        String requestPath = ctx.request.getUri();
        String[] requestSegments = normalizePath(requestPath).split("/");
        for (Route route : routes) {
            if (!matchMethod(route.getMethods(), ctx.request.getRequestMethod())){
                throw new MethodNotSupportException();
            }
            if (matchRoute(route, requestSegments, ctx)) {
                route.getHandler().handle(ctx);
                return true;
            }
        }
        return false; // 没有匹配的路由
    }

    private boolean matchMethod(HttpMethod[] routeMethods, HttpMethod requestMethod) {
        for (HttpMethod routeMethod : routeMethods) {
            if (routeMethod.equals(requestMethod)) {
                return true;
            }
        }
        return false;
    }

    // 确定路由类型
    private Route.Type determineRouteType(String path) {
        if (path.contains("*")) {
            return Route.Type.WILDCARD;
        } else if (path.contains(":")) {
            return Route.Type.PATH_PARAM;
        } else {
            return Route.Type.EXACT;
        }
    }

    // 标准化路径
    private String normalizePath(String path) {
        if (path == null || path.isEmpty()) {
            return "/";
        }
        return path.replaceAll("//+", "/").replaceAll("/$", "");
    }

    // 匹配路由
    private boolean matchRoute(Route route, String[] requestSegments, WebContext ctx) {
        String[] routeSegments = route.getPathSegments();

        // 通配符路由特殊处理
        if (route.getType() == Route.Type.WILDCARD) {
            return matchWildcardRoute(routeSegments, requestSegments);
        }

        // 段数不同，直接不匹配
        if (routeSegments.length != requestSegments.length) {
            return false;
        }

        // 检查每一段是否匹配
        for (int i = 0; i < routeSegments.length; i++) {
            String routeSeg = routeSegments[i];
            String reqSeg = requestSegments[i];

            // 路径参数匹配
            if (route.getType() == Route.Type.PATH_PARAM && routeSeg.startsWith(":")) {
                String paramName = routeSeg.substring(1);
                ctx.request.addUrlParams(paramName, reqSeg);
                continue;
            }

            // 精确匹配失败
            if (!routeSeg.equals(reqSeg)) {
                return false;
            }
        }

        return true;
    }

    // 修复后的通配符路由匹配方法
    private boolean matchWildcardRoute(String[] routeSegments, String[] requestSegments) {
        int routeIndex = 0;
        int requestIndex = 0;
        int routeLength = routeSegments.length;
        int requestLength = requestSegments.length;

        while (routeIndex < routeLength && requestIndex < requestLength) {
            String currentRouteSeg = routeSegments[routeIndex];

            if (currentRouteSeg.equals("*")) {
                // 如果是最后一个路由段，匹配剩余所有请求段
                if (routeIndex == routeLength - 1) {
                    return true;
                } else {
                    // 中间通配符匹配单个请求段，继续匹配下一段
                    routeIndex++;
                    requestIndex++;
                }
            } else {
                // 非通配符段需要精确匹配
                if (!currentRouteSeg.equals(requestSegments[requestIndex])) {
                    return false;
                }
                routeIndex++;
                requestIndex++;
            }
        }

        // 只有当双方都遍历完所有段时才匹配（除了最后一个通配符的情况）
        return routeIndex == routeLength && requestIndex == requestLength;
    }
    // 注册控制器中的路由
    public void registerControllerRoutes(Class<?> controllerClass) throws InstantiationException, IllegalAccessException {
        Object controllerInstance = IocApplicationContext.getBean(controllerClass);
        Controller annotation = controllerClass.getAnnotation(Controller.class);
        Method[] methods = controllerClass.getDeclaredMethods();
        for (Method method : methods) {
            Map<String, List<HttpMethod>> httpMethodAnnotations = AnnotationIntersection.findHttpMethodAnnotations(annotation.value(),method);
            if (httpMethodAnnotations.isEmpty()) {
                continue;
            }

            String[] methodsParamNames = ParameterUtil.getMethodsParamNames(method);

            httpMethodAnnotations.forEach((key, value) -> {
                Handler handler = ctx -> {
                    Object[] methodArgs = ParameterUtil.getMethodArgs(method, methodsParamNames, ctx);
                    Object res = method.invoke(controllerInstance,methodArgs);
                    //调用结果进行设置
                    if (res == null) {
                        if (!ctx.response.hasData()) {
                            ctx.response.sendText("");
                        }
                    } else if (res instanceof String) {
                        ctx.response.sendText(res.toString());
                    } else {
                        ctx.response.sendJson(res);
                    }
                };
                route(key, handler,value.toArray(new HttpMethod[0]));
            });
        }
    }
}
