package top.hserver.cloud.bean;

import lombok.Data;

import java.lang.reflect.Method;

@Data
public class ClientData {
    private Class aClass;
    private String className;
    private Method[] methods;
}
