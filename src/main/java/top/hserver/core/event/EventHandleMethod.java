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

    private Method method;
    private String uri;
    private String className;
    private Class<?>[] parameterTypes;

    public EventHandleMethod(String className, Method method, String uri) {
        this.method = method;
        this.uri = uri;
        this.className = className;
        this.parameterTypes = method.getParameterTypes();
    }

    public Object getHandler() {
        return IocUtil.getBean(this.className);
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

}
