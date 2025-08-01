package cn.hserver.mvc.websoket;

import cn.hserver.mvc.constants.WsType;
import cn.hserver.mvc.request.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;


/**
 * @author hxm
 */
public abstract class Ws {

    private final String message;
    private final byte[] binary;
    private final String uid;
    private final Request request;
    private final WsType type;

    public Ws(byte[] binary,String message, String uid, Request request, WsType type) {
        this.binary = binary;
        this.uid = uid;
        this.request = request;
        this.type=type;
        this.message = message;
    }

    public WsType getType() {
        return type;
    }

    public void send(String msg){
        send(msg, null);
    }
    public  void send(byte[] msg){
        send(msg, null);
    }
    public  void sendBinary(byte[] msg){
        sendBinary(msg, null);
    }
    public abstract void send(String msg, Consumer<Boolean> callback);

    public abstract void send(byte[] msg,Consumer<Boolean> callback);

    public abstract void sendBinary(byte[] msg, Consumer<Boolean> callback);




    public String query(String name) {
        return request.query(name);
    }

    public String getMessage() {
        return message;
    }

    public String getUid() {
        return uid;
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
}
