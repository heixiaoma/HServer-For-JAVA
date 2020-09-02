package top.hserver.core.event;

import lombok.Data;
import top.hserver.core.ioc.IocUtil;

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
    private Method method;
    private Object args;
    private String className;


    public EventHandleMethod() {
    }

    public EventHandleMethod(String className, Method method, Object args, int level) {
        this.method = method;
        this.className=className;
        this.args = args;
        this.level=level;
    }

    public Object getHandler() {
        return IocUtil.getBean(this.className);
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object getArgs() {
        return args;
    }

    public void setArgs(Object args) {
        this.args = args;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
