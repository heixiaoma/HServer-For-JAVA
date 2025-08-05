package cn.hserver.mvc.router;

import cn.hserver.mvc.context.WebContext;

import java.lang.reflect.InvocationTargetException;


/**
 * 路由处理器接口，使用函数式接口方便lambda表达式使用
 */
@FunctionalInterface
public interface Handler {
    void handle(WebContext ctx) throws Throwable;
}
