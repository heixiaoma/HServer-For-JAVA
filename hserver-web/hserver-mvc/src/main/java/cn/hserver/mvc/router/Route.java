package cn.hserver.mvc.router;

import cn.hserver.mvc.constants.HttpMethod;

/**
 * 路由信息封装类，包含路径、处理器和路由类型
 */
public class Route {

    public enum Type {
        EXACT,       // 精确匹配
        PATH_PARAM,  // 路径参数匹配
        WILDCARD     // 通配符匹配
    }

    private final String path;
    private final Handler handler;
    private final Type type;
    private final String[] pathSegments; // 路径分段，用于匹配
    private final HttpMethod[] methods;

    public Route(String path, Handler handler, Type type,HttpMethod[] methods) {
        this.path = path;
        this.handler = handler;
        this.type = type;
        this.methods = methods;
        this.pathSegments = normalizePath(path).split("/");
    }

    // 标准化路径，移除多余的斜杠
    private String normalizePath(String path) {
        if (path == null || path.isEmpty()) {
            return "/";
        }
        return path.replaceAll("//+", "/").replaceAll("/$", "");
    }

    public HttpMethod[] getMethods() {
        return methods;
    }

    // getter 方法
    public String getPath() {
        return path;
    }

    public Handler getHandler() {
        return handler;
    }

    public Type getType() {
        return type;
    }

    public String[] getPathSegments() {
        return pathSegments;
    }

}
