package top.hserver.core.server.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author hxm
 */
public class Ws {
    private ChannelHandlerContext ctx;
    private String message;
    private String uid;
    private HttpRequest request;
    private Map<String, List<String>> reqData = new HashMap<>();

    public Ws(ChannelHandlerContext ctx, String uid, HttpRequest request) {
        this.ctx = ctx;
        this.uid = uid;
        this.request = request;
        initReqData();
    }

    public Ws(ChannelHandlerContext ctx, String message, String uid, HttpRequest request) {
        this.ctx = ctx;
        this.message = message;
        this.uid = uid;
        this.request = request;
        initReqData();
    }

    public void send(String msg) {
        this.ctx.writeAndFlush(new TextWebSocketFrame(msg));
    }

    public void send(byte[] msg) {
        this.ctx.writeAndFlush(new TextWebSocketFrame(Unpooled.wrappedBuffer(msg)));
    }

    public void sendBinary(byte[] msg) {
        this.ctx.writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(msg)));
    }

    public void sendBinary(ByteBuf msg) {
        this.ctx.writeAndFlush(new BinaryWebSocketFrame(msg));
    }

    public String query(String name) {
        return reqData.get(name) != null ? reqData.get(name).get(0) : null;
    }


    private void initReqData() {
        if (request!=null) {
            QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
            Map<String, List<String>> params = decoder.parameters();
            for (Map.Entry<String, List<String>> next : params.entrySet()) {
                reqData.put(next.getKey(), next.getValue());
            }
        }
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
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

    public HttpRequest getRequest() {
        return request;
    }

    public void setRequest(HttpRequest request) {
        this.request = request;
    }

    public Map<String, List<String>> getReqData() {
        return reqData;
    }

    public void setReqData(Map<String, List<String>> reqData) {
        this.reqData = reqData;
    }
}
