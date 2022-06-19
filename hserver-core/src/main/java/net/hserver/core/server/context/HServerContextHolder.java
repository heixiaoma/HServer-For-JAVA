package net.hserver.core.server.context;

import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * @author hxm
 */
public class HServerContextHolder {

    private static  final TransmittableThreadLocal<Webkit> WEBKIT_INHERITABLE_THREAD_LOCAL = new TransmittableThreadLocal<>();

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
