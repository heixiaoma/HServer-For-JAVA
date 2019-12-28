package top.hserver.cloud.bean;

import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.Method;

@Data
public class InvokeServiceData implements Serializable {

    private static final long SerialVersionUID = 1L;

    private String aClass;//类名

    private String method;

    private Object[] objects; //参数列表

}
