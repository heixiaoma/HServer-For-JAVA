package cn.hserver.core.server.util;

import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;

/**
 * 释放对象
 */
public class ReleaseUtil {
    public static boolean release(Object msg) {
        if (msg == null) {
            return false;
        }
        if (msg instanceof ReferenceCounted) {
            ReferenceCounted msg1 = (ReferenceCounted) msg;
            if (msg1.refCnt() > 0) {
                if (((ReferenceCounted) msg).refCnt() > 0) {
                    ReferenceCountUtil.release(msg);
                    return true;
                }
            }
        }
        return false;
    }
}
