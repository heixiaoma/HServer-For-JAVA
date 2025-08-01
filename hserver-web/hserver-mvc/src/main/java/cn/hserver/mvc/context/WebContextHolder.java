package cn.hserver.mvc.context;


/**
 * @author hxm
 */
public class WebContextHolder {

    private static  final ThreadLocal<WebContext> WEBKIT_INHERITABLE_THREAD_LOCAL = new ThreadLocal<>();

    public static void setWebContext(WebContext webKit) {
        WEBKIT_INHERITABLE_THREAD_LOCAL.set(webKit);
    }

    public static WebContext getWebContext() {
        return WEBKIT_INHERITABLE_THREAD_LOCAL.get();
    }

    public static void remove() {
        WEBKIT_INHERITABLE_THREAD_LOCAL.remove();
    }
}
