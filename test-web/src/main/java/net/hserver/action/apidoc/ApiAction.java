package net.hserver.action.apidoc;

import net.hserver.TestWebApp;
import top.hserver.core.api.ApiDoc;
import top.hserver.core.api.ApiResult;
import top.hserver.core.interfaces.HttpResponse;
import top.hserver.core.ioc.annotation.Controller;
import top.hserver.core.ioc.annotation.GET;
import top.hserver.core.server.util.JsonResult;

import java.util.HashMap;
import java.util.List;

@Controller
public class ApiAction {

  @GET("/api")
  public void getApiData(HttpResponse httpResponse) {
    ApiDoc apiDoc = new ApiDoc(TestWebApp.class);
    try {
      List<ApiResult> apiData = apiDoc.getApiData();
      HashMap<String,Object> stringObjectHashMap=new HashMap<>();
      stringObjectHashMap.put("data",apiData);
      httpResponse.sendTemplate("hserver_doc.ftl",stringObjectHashMap);
    }catch (Exception e){
      httpResponse.sendJson(JsonResult.error());
    }
  }

  @GET("/apiJson")
  public JsonResult getApiDataa() {
    ApiDoc apiDoc = new ApiDoc("top.test");
    try {
      List<ApiResult> apiData = apiDoc.getApiData();
      return JsonResult.ok().put("data",apiData);
    }catch (Exception e){
      return JsonResult.error();
    }
  }

}
