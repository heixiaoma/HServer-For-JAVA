package com.hserver.core.server.context;

import io.netty.handler.codec.http.HttpMethod;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class Request {
    private String uri;
    private HttpMethod requestType;
    private Map<String,String> requestParams;


}
