package net.hserver.core.queue;

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

    private int size;

    private Method method;

    public QueueHandleMethod(Method method, int level,int size) {
        this.method = method;
        this.level = level;
        this.size=size;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
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
