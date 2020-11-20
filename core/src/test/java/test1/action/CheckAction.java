package test1.action;

import top.hserver.core.ioc.annotation.*;
import top.hserver.core.server.util.JsonResult;

@Controller
public class CheckAction {

    @Sign("签名验证")
    @RequiresRoles("角色")
    @RequiresPermissions(value = {"/权限1","/权限2"}, logical=Logical.OR)
    @GET("/sign")
    public JsonResult sign() {
        return JsonResult.ok();
    }

}
