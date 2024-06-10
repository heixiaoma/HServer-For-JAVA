package cn.hserver.plugin.web.router;


import io.netty.handler.codec.http.HttpMethod;

import java.lang.reflect.Method;

/**
 * @author hxm
 */
public class RouterInfo {

    private Object controllerRef;
    private String url;
    private Method method;
    private HttpMethod reqMethodName;
    private Class<?> aClass;

    public Object getControllerRef() {
        return controllerRef;
    }

    public void setControllerRef(Object controllerRef) {
        this.controllerRef = controllerRef;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public HttpMethod getReqMethodName() {
        return reqMethodName;
    }

    public void setReqMethodName(HttpMethod reqMethodName) {
        this.reqMethodName = reqMethodName;
    }

    public Class<?> getaClass() {
        return aClass;
    }

    public void setaClass(Class<?> aClass) {
        this.aClass = aClass;
    }


}
