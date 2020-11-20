package test1.action;

import top.hserver.core.interfaces.HttpRequest;
import top.hserver.core.ioc.annotation.*;
import top.hserver.core.server.util.JsonResult;

@Controller
public class MethodAction {

  @RequestMapping("/all")
  public JsonResult all() {
    return JsonResult.ok();
  }

  @RequestMapping(value = "/get", method = RequestMethod.GET)
  public JsonResult get() {
    return JsonResult.ok();
  }

  @RequestMapping(value = "/post", method = RequestMethod.POST)
  public JsonResult post(HttpRequest httpRequest) {
    return JsonResult.ok().put("data",httpRequest.getRawData());
  }

  @RequestMapping(value = "/PUT", method = RequestMethod.PUT)
  public JsonResult PUT(HttpRequest httpRequest) {
    return JsonResult.ok().put("data",httpRequest.getMultipartFile());
  }

  @RequestMapping(value = "/OPTIONS", method = RequestMethod.OPTIONS)
  public JsonResult OPTIONS() {
    return JsonResult.ok();
  }

  @Sign("签名验证")
  @RequiresRoles("角色")
  @RequiresPermissions(value = {"/权限1","/权限2"}, logical=Logical.OR)
  @RequestMapping(value = "/PUT_OPTIONS", method = {RequestMethod.OPTIONS, RequestMethod.PUT})
  public JsonResult PUT_OPTIONS() {
    return JsonResult.ok();
  }

}
