package test1.action;

import top.hserver.core.ioc.annotation.*;
import top.hserver.core.server.util.JsonResult;

@Controller
public class ReqAction {

  @GET("/req1")
  public JsonResult GET() {
    return JsonResult.ok();
  }

  @HEAD("/req2")
  public JsonResult HEAD() {
    return JsonResult.ok();
  }

  @POST("/req3")
  public JsonResult POST() {
    return JsonResult.ok();
  }

  @PUT("/req4")
  public JsonResult PUT() {
    return JsonResult.ok();
  }

  @TRACE("/req5")
  public JsonResult TRACE() {
    return JsonResult.ok();
  }

  @PATCH("/req6")
  public JsonResult PATCH() {
    return JsonResult.ok();
  }

  @DELETE("/req7")
  public JsonResult DELETE() {
    return JsonResult.ok();
  }

  @Sign("签名验证")
  @RequiresRoles("角色")
  @RequiresPermissions(value = {"/权限1","/权限2"}, logical=Logical.OR)
  @OPTIONS("/req8")
  public JsonResult OPTIONS() {
    return JsonResult.ok();
  }


  @CONNECT("/req9")
  public JsonResult CONNECT() {
    return JsonResult.ok();
  }

}
