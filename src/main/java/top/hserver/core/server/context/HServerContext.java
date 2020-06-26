package top.hserver.core.server.context;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;

import java.util.ArrayList;
import java.util.List;


/**
 * @author hxm
 */
public class HServerContext {

    private Webkit webkit;

    private Request request;

    private Response response;

    private boolean isStaticFile;

    private boolean isFilter;

    private FullHttpRequest fullHttpRequest;

    private StaticFile staticFile;

    private String result;

    private ChannelHandlerContext ctx;

    private List<ByteBuf> byteBufs;

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public boolean isStaticFile() {
        return isStaticFile;
    }

    public void setStaticFile(boolean staticFile) {
        isStaticFile = staticFile;
    }

    public FullHttpRequest getFullHttpRequest() {
        return fullHttpRequest;
    }

    public void setFullHttpRequest(FullHttpRequest fullHttpRequest) {
        this.fullHttpRequest = fullHttpRequest;
    }

    public StaticFile getStaticFile() {
        return staticFile;
    }

    public void setStaticFile(StaticFile staticFile) {
        this.staticFile = staticFile;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public boolean isFilter() {
        return isFilter;
    }

    public void setFilter(boolean filter) {
        isFilter = filter;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public Webkit getWebkit() {
        return webkit;
    }

    public void setWebkit(Webkit webkit) {
        this.webkit = webkit;
    }

    public List<ByteBuf> getByteBufs() {
        return byteBufs;
    }

    public void addByteBuf(ByteBuf byteBuf) {
        if (this.byteBufs==null){
            this.byteBufs=new ArrayList<>();
        }
        this.byteBufs.add(byteBuf);
    }
}
