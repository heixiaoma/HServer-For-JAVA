package cn.hserver.plugin.web.context;

import io.netty.util.concurrent.FastThreadLocal;

/**
 * @author hxm
 */
public class HServerContextHolder {

    private static  final FastThreadLocal<Webkit> WEBKIT_INHERITABLE_THREAD_LOCAL = new FastThreadLocal<>();

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
