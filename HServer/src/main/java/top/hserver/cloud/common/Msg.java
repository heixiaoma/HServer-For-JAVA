package top.hserver.cloud.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class Msg<T> implements Serializable {

    private static final long SerialVersionUID = 1L;

    private MSG_TYPE msg_type;

    private T data;


}
