package top.hserver.core.queue;

import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * 事件处理方法
 *
 * @author hxm
 */
@Data
public class QueueHandleMethod implements Serializable {
    /**
     * 级别
     */
    private int level;
    private int size;
    private Method method;
    private boolean isTry;

    public QueueHandleMethod(Method method, int size, int level,boolean isTry) {
        this.method = method;
        this.level = level;
        this.size = size;
        this.isTry=isTry;
    }

    public int getLevel() {
        return level;
    }

    public Method getMethod() {
        return method;
    }

    public int getSize() {
        return size;
    }

    public boolean isTry() {
        return isTry;
    }
}
