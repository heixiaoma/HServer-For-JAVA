package cn.hserver.plugin.rpc.codec;

import java.io.Serializable;

public class Msg<T> implements Serializable {
    private static final long SerialVersionUID = 1L;
    private MsgType msgType;
    private T data;

    public Msg() {
    }

    public Msg(MsgType msgType) {
        this.msgType = msgType;
    }

    public MsgType getMsgType() {
        return msgType;
    }

    public void setMsgType(MsgType msgType) {
        this.msgType = msgType;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
