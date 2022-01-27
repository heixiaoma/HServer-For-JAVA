package top.hserver.core.server.util;

import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.Stack;

public class SpanUtil {

    private static final TransmittableThreadLocal<Stack<Integer>> threadMethods = new TransmittableThreadLocal<>();

    public static void add() {
        Stack<Integer> queue = null;
        if (null == threadMethods.get()) {
            queue = new Stack<>();
            queue.add(0);
        } else {
            queue = threadMethods.get();
            queue.add(queue.peek()+1);
        }
        threadMethods.set(queue);
    }

    public static int get() {
        Stack<Integer> integers = threadMethods.get();
        if (integers == null) {
            return 0;
        }
        return integers.peek();
    }

    public static void clear() {
        Stack<Integer> queue = threadMethods.get();
        if (queue == null) {
            return;
        }
        queue.pop();
        if (queue.isEmpty()) {
            threadMethods.remove();
        }
    }

}