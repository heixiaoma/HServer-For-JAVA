package top.hserver.cloud.common;


import java.io.Serializable;

public class Msg<T> implements Serializable {

    private static final long SerialVersionUID = 1L;

    private MSG_TYPE msg_type;

    private T data;

    public MSG_TYPE getMsg_type() {
        return msg_type;
    }

    public void setMsg_type(MSG_TYPE msg_type) {
        this.msg_type = msg_type;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
