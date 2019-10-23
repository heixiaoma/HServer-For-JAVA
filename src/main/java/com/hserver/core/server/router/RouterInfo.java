package com.hserver.core.server.router;


import java.lang.reflect.Method;

public class RouterInfo {

    String url;
    Method method;
    RequestType reqMethodName;
    Class<?> aClass;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Class<?> getaClass() {
        return aClass;
    }

    public void setaClass(Class<?> aClass) {
        this.aClass = aClass;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public RequestType getReqMethodName() {
        return reqMethodName;
    }

    public void setReqMethodName(RequestType reqMethodName) {
        this.reqMethodName = reqMethodName;
    }
}
