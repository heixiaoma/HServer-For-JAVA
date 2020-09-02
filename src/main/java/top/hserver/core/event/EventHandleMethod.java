package top.hserver.core.event;

import lombok.Data;
import top.hserver.core.ioc.IocUtil;
import top.hserver.core.ioc.annotation.event.EventHandlerType;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * 事件处理方法
 *
 * @author hxm
 */
@Data
public class EventHandleMethod implements Serializable {
    /**
     * 级别
     */
    private int level;
    private int size;
    private Method method;

    public EventHandleMethod(Method method, int size, int level) {
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
