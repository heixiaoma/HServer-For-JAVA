package top.hserver.core.queue;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * 事件处理方法
 *
 * @author hxm
 */
public class QueueHandleMethod implements Serializable {
    /**
     * 级别
     */
    private int level;
    private Method method;

    public QueueHandleMethod(Method method, int level) {
        this.method = method;
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public Method getMethod() {
        return method;
    }

    public void setLevel(int level) {
        this.level = level;
    }


    public void setMethod(Method method) {
        this.method = method;
    }
}
