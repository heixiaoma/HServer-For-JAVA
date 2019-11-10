package com.hserver.core.server.context;


import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.Queue;


public class WebContext {

    private Request request;

    private Response response;

    private boolean isStaticFile;

    private HttpRequest httpRequest;

    private StaticFile staticFile;

    private String result;

    private Queue<HttpContent> contents = new LinkedList<>();

    public void appendContent(HttpContent msg) {
        this.contents.add(msg.retain());
    }

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

    public HttpRequest getHttpRequest() {
        return httpRequest;
    }

    public void setHttpRequest(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
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

    public Queue<HttpContent> getContents() {
        return contents;
    }

    public void setContents(Queue<HttpContent> contents) {
        this.contents = contents;
    }
}
