package cn.hserver.mvc.context;


/**
 * @author hxm
 */
public class HServerContextHolder {

    private static  final ThreadLocal<Webkit> WEBKIT_INHERITABLE_THREAD_LOCAL = new ThreadLocal<>();

    public static void setWebKit(Webkit webKit) {
        WEBKIT_INHERITABLE_THREAD_LOCAL.set(webKit);
    }

    public static Webkit getWebKit() {
        return WEBKIT_INHERITABLE_THREAD_LOCAL.get();
    }

    public static void remove() {
        WEBKIT_INHERITABLE_THREAD_LOCAL.remove();
    }
}
