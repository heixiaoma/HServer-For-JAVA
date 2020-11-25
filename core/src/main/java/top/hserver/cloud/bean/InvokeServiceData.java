package top.hserver.cloud.bean;

import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * @author hxm
 */
@Data
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

}
