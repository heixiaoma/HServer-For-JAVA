package top.hserver.cloud.bean;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.Data;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;

@Data
public class ResultData implements Serializable {

    private static final long SerialVersionUID = 1L;

    private String requestId;

    private HttpResponseStatus code;

    private Object data;

    private Throwable error;
}
