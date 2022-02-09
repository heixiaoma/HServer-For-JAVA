package top.hserver.core.server.util;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hserver.HServerApplication;

import java.util.Stack;

public class SpanUtil {
    private static final Logger log = LoggerFactory.getLogger(SpanUtil.class);

    private static final SnowflakeIdWorker SNOWFLAKE_ID_WORKER=new SnowflakeIdWorker(1,30);
    private static final TransmittableThreadLocal<Stack<Long>> threadMethods = new TransmittableThreadLocal<>();

    public static void add() {
        Stack<Long> queue;
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
            log.error(ExceptionUtil.getMessage(e));
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