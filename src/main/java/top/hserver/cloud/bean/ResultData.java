package top.hserver.cloud.bean;

import lombok.Data;

import java.io.Serializable;

@Data
public class ResultData implements Serializable {

    private static final long SerialVersionUID = 1L;

    private String requestId;

    private int code;

    private Object data;

    private Throwable error;
}
