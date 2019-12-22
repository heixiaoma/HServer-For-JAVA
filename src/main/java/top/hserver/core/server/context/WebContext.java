package top.hserver.core.server.context;


import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import top.hserver.core.interfaces.Statistics;

import java.util.LinkedList;
import java.util.Queue;


public class WebContext {

    private Webkit webkit;

    private Request request;

    private Response response;

    private boolean isStaticFile;

    private boolean isFilter;

    private HttpRequest httpRequest;

    private StaticFile staticFile;

    private String result;

    private ChannelHandlerContext ctx;

    //时间统计
    private Statistics statistics;

    private Queue<HttpContent> contents = new LinkedList<>();


    public void regStatistics(Statistics statistics) {
        this.statistics = statistics;
    }

    public void stopStatistics(long stopTime) {
        if (this.statistics != null) {
            this.statistics.stopStatistics(stopTime);
        }
    }



    /**
     * 多消息内容存起来，一会构建对象的时候编码然后取出文件或者字段或者数据
     *
     * @param msg
     */
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
}
