package test8;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpHeaders;
import top.hserver.core.server.util.ByteBufUtil;

import java.util.concurrent.CompletableFuture;


public class HFuture extends CompletableFuture<HFuture> {

    private byte[] data;
    private HttpHeaders httpHeaders;
    private HResponse.Listener listener;
    private Throwable e;
    private int statusCode;

    public HFuture() {
    }

    public HFuture(HResponse.Listener listener) {
        this.listener = listener;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void write(ByteBuf byteBuf) {
        this.data = ByteBufUtil.byteBufToBytes(byteBuf);
    }

    public void success() {
        if (this.listener != null) {
            this.listener.complete(new HResp(data, httpHeaders, e, statusCode));
        } else {
            super.complete(this);
        }
    }

    public HResponse getResponse() {
        return new HResp(data, httpHeaders, e, statusCode);
    }

    public void setHttpHeaders(HttpHeaders httpHeaders) {
        this.httpHeaders = httpHeaders;
    }

    public void error(Throwable e) {
        this.e = e;
        if (this.listener != null) {
            this.listener.exception(e);
        } else {
            super.complete(this);
        }
    }

}
