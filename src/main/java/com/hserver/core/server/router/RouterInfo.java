package com.hserver.core.server.router;


import io.netty.handler.codec.http.HttpMethod;

import java.lang.reflect.Method;

public class RouterInfo {

    String url;
    Method method;
    HttpMethod reqMethodName;
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

    public HttpMethod getReqMethodName() {
        return reqMethodName;
    }

    public void setReqMethodName(HttpMethod reqMethodName) {
        this.reqMethodName = reqMethodName;
    }
}
