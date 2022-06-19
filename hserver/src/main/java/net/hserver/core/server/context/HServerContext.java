package net.hserver.core.server.context;



/**
 * @author hxm
 */
public class HServerContext {

    private Webkit webkit;

    private Request request;

    private Response response;

    private boolean isStaticFile;

    private StaticFile staticFile;

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

    public StaticFile getStaticFile() {
        return staticFile;
    }

    public void setStaticFile(StaticFile staticFile) {
        this.staticFile = staticFile;
    }

    public Webkit getWebkit() {
        return webkit;
    }

    public void setWebkit(Webkit webkit) {
        this.webkit = webkit;
    }

}
