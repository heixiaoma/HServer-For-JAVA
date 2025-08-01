package cn.hserver.mvc.websoket;

import cn.hserver.mvc.constants.WsType;
import cn.hserver.mvc.request.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;


/**
 * @author hxm
 */
public abstract class Ws {
    private static final Logger log = LoggerFactory.getLogger(Ws.class);

    private String message;
    private byte[] binary;
    private String uid;
    private final Request request;
    private WsType type;

    public Ws(String uid, Request request, WsType type) {
        this.uid = uid;
        this.request = request;
        this.type=type;
    }

    public Ws(String message, String uid, Request request, WsType type) {
        this.message = message;
        this.uid = uid;
        this.request = request;
        this.type=type;
    }

    public Ws(byte[] binary, String uid, Request request, WsType type) {
        this.binary = binary;
        this.uid = uid;
        this.request = request;
        this.type=type;
    }

    public WsType getType() {
        return type;
    }

    public void setType(WsType type) {
        this.type = type;
    }

    public abstract void send(String msg);

    public abstract void send(byte[] msg);

    public  abstract void sendBinary(byte[] msg);

    public String query(String name) {
        return request.query(name);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Request getRequest() {
        return request;
    }

    public Map<String, List<String>> getReqData() {
        return request.getRequestParams();
    }

    public byte[] getBinary() {
        return binary;
    }

    public void setBinary(byte[] binary) {
        this.binary = binary;
    }
}
