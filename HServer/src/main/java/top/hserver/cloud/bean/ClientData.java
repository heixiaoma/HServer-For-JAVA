package top.hserver.cloud.bean;


import java.lang.reflect.Method;

/**
 * @author hxm
 */
public class ClientData {

    private String aClass;
    private String className;
    private Method[] methods;

    public String getaClass() {
        return aClass;
    }

    public void setaClass(String aClass) {
        this.aClass = aClass;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Method[] getMethods() {
        return methods;
    }

    public void setMethods(Method[] methods) {
        this.methods = methods;
    }
}
