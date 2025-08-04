package cn.hserver.mvc.context;


import cn.hserver.mvc.request.Request;
import cn.hserver.mvc.response.Response;

/**
 * web 上下文
 *
 * @author hxm
 */
public class WebContext {

    public Request request;

    public Response response;

    public WebContext(Request request, Response response) {
        this.request = request;
        this.response = response;
    }




}
