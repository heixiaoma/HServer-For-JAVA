package cn.hserver.core.server.util;

import io.netty.util.concurrent.FastThreadLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Stack;

public class SpanUtil {
    private static final Logger log = LoggerFactory.getLogger(SpanUtil.class);

    private static final SnowflakeIdWorker SNOWFLAKE_ID_WORKER = new SnowflakeIdWorker(1, 30);
    private static final FastThreadLocal<Stack<Long>> threadMethods = new FastThreadLocal<>();

    public static long add() {
        long l = SNOWFLAKE_ID_WORKER.nextId();
        Stack<Long> queue;
        if (null == threadMethods.get()) {
            queue = new Stack<>();
            queue.add(l);
        } else {
            queue = threadMethods.get();
            queue.add(l);
        }
        threadMethods.set(queue);
        return l;
    }

    public static long get() {
        try {
            Stack<Long> integers = threadMethods.get();
            if (integers == null||integers.isEmpty()) {
                return -1;
            }
            return integers.peek();
        } catch (Exception e) {
            log.error(e.getMessage(),e);

            return -1;
        }
    }

    public static void clear() {
        Stack<Long> queue = threadMethods.get();
        if (queue == null) {
            return;
        }
        if (!queue.isEmpty()){
            queue.pop();
        }
        if (queue.isEmpty()) {
            threadMethods.remove();
        }
    }

}
