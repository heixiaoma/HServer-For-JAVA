package test1.action;


import io.netty.handler.codec.http.HttpResponseStatus;
import top.hserver.core.interfaces.HttpResponse;
import top.hserver.core.ioc.annotation.Controller;
import top.hserver.core.ioc.annotation.GET;

@Controller
public class StatusCodeAction {

  @GET("/status1")
  public void statu1(HttpResponse httpResponse){
    httpResponse.sendStatusCode(HttpResponseStatus.NOT_FOUND);
    httpResponse.sendText("ttt");
  }


  @GET("/status2")
  public String statu2(HttpResponse httpResponse){
    httpResponse.sendStatusCode(HttpResponseStatus.SERVICE_UNAVAILABLE);
    return "000";
  }

}
