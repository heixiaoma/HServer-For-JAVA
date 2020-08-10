package top.hserver.cloud.bean;

import lombok.Data;

import java.io.Serializable;

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
     *  类名
     */
    private String aClass;

    /**
     * 方法
     */
    private String method;

    /**
     * 参数列表
     */
    private Object[] objects;

    /**
     * 是否是心跳
     */
    private boolean isPingPing;

}
