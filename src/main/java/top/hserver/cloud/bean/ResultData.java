package top.hserver.cloud.bean;

import lombok.Data;

import java.io.Serializable;

@Data
public class ResultData<T> implements Serializable {

    private static final long SerialVersionUID = 1L;

    private String UUID;

    private int code;

    private T data;
}
