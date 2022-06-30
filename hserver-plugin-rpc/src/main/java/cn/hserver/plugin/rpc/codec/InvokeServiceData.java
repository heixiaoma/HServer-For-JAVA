package cn.hserver.plugin.rpc.codec;

import java.io.Serializable;

public class InvokeServiceData implements Serializable {
    private static final long SerialVersionUID = 1L;
    private String requestId;
    private String aClass;
    private String method;
    private Class[] parameterTypes;
    private Object[] objects;
    private String serverName;

    public InvokeServiceData() {
    }

    public String getRequestId() {
        return this.requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getaClass() {
        return this.aClass;
    }

    public void setaClass(String aClass) {
        this.aClass = aClass;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Class[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Object[] getObjects() {
        return this.objects;
    }

    public void setObjects(Object[] objects) {
        this.objects = objects;
    }

    public String getServerName() {
        return this.serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
}
