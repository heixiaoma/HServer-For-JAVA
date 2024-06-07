package cn.hserver.plugin.web.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.hserver.plugin.web.context.WsType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author hxm
 */
public class Ws {
    private static final Logger log = LoggerFactory.getLogger(Ws.class);

    private ChannelHandlerContext ctx;
    private String message;
    private byte[] binary;
    private String uid;
    private HttpRequest request;
    private Map<String, List<String>> reqData = new HashMap<>();
    private WsType type;

    public Ws(ChannelHandlerContext ctx, String uid, HttpRequest request,WsType type) {
        this.ctx = ctx;
        this.uid = uid;
        this.request = request;
        this.type=type;
        initReqData();
    }

    public Ws(ChannelHandlerContext ctx, String message, String uid, HttpRequest request, WsType type) {
        this.ctx = ctx;
        this.message = message;
        this.uid = uid;
        this.request = request;
        this.type=type;
        initReqData();
    }

    public Ws(ChannelHandlerContext ctx, byte[] binary, String uid, HttpRequest request,WsType type) {
        this.ctx = ctx;
        this.binary = binary;
        this.uid = uid;
        this.request = request;
        this.type=type;
        initReqData();
    }

    public WsType getType() {
        return type;
    }

    public void setType(WsType type) {
        this.type = type;
    }

    public ChannelFuture send(String msg) {
       return this.ctx.writeAndFlush(new TextWebSocketFrame(msg));
    }

    public ChannelFuture send(byte[] msg) {
       return this.ctx.writeAndFlush(new TextWebSocketFrame(Unpooled.wrappedBuffer(msg)));
    }

    public ChannelFuture sendBinary(byte[] msg) {
       return this.ctx.writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(msg)));
    }

    public ChannelFuture sendBinary(ByteBuf msg) {
       return this.ctx.writeAndFlush(new BinaryWebSocketFrame(msg));
    }

    public String query(String name) {
        return reqData.get(name) != null ? reqData.get(name).get(0) : null;
    }


    private void initReqData() {
        if (request != null) {
            try {
                QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
                Map<String, List<String>> params = decoder.parameters();
                for (Map.Entry<String, List<String>> next : params.entrySet()) {
                    reqData.put(next.getKey(), next.getValue());
                }
            } catch (Exception e) {
                log.warn(e.getMessage());
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

    public byte[] getBinary() {
        return binary;
    }

    public void setBinary(byte[] binary) {
        this.binary = binary;
    }
}
