package com.hserver.core.server.context;


public class WebContext {

    private Request request;

    private Response response;

    private boolean isStaticFile;

    private StaticFile staticFile;

    private String result;


    public boolean isStaticFile() {
        return isStaticFile;
    }

    public void setStaticFile(boolean staticFile) {
        isStaticFile = staticFile;
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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public StaticFile getStaticFile() {
        return staticFile;
    }

    public void setStaticFile(StaticFile staticFile) {
        this.staticFile = staticFile;
    }
}
