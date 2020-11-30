package top.hserver.cloud.bean;


import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * @author hxm
 */
public class InvokeServiceData implements Serializable {

    private static final long SerialVersionUID = 1L;

    /**
     * 调用标识
     */
    private String requestId;

    /**
     * 类名
     */
    private String aClass;

    /**
     * 方法
     */
    private Method method;

    /**
     * 参数列表
     */
    private Object[] objects;

    /**
     * 服务名
     */
    private String serverName;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getaClass() {
        return aClass;
    }

    public void setaClass(String aClass) {
        this.aClass = aClass;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object[] getObjects() {
        return objects;
    }

    public void setObjects(Object[] objects) {
        this.objects = objects;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
}
