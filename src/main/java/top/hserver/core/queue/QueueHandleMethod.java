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

    public QueueHandleMethod(Method method, int size, int level) {
        this.method = method;
        this.level = level;
        this.size = size;
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
}
