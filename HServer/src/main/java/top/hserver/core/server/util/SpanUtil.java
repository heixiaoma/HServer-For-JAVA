package top.hserver.core.server.util;

import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.Stack;

public class SpanUtil {

    private static final SnowflakeIdWorker SNOWFLAKE_ID_WORKER=new SnowflakeIdWorker(1,30);
    private static final TransmittableThreadLocal<Stack<Long>> threadMethods = new TransmittableThreadLocal<>();

    public static void add() {
        Stack<Long> queue = null;
        if (null == threadMethods.get()) {
            queue = new Stack<>();
            queue.add(SNOWFLAKE_ID_WORKER.nextId());
        } else {
            queue = threadMethods.get();
            queue.add(queue.peek()+1);
        }
        threadMethods.set(queue);
    }

    public static long get() {
        try {
            Stack<Long> integers = threadMethods.get();
            if (integers == null) {
                return SNOWFLAKE_ID_WORKER.nextId();
            }
            return integers.peek();
        }catch (Exception e){
            return -1;
        }
    }

    public static void clear() {
        Stack<Long> queue = threadMethods.get();
        if (queue == null) {
            return;
        }
        queue.pop();
        if (queue.isEmpty()) {
            threadMethods.remove();
        }
    }

}