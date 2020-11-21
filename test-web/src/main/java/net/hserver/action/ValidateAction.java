package net.hserver.action;
import net.hserver.bean.User;
import top.hserver.core.ioc.annotation.Controller;
import top.hserver.core.ioc.annotation.RequestMapping;
import top.hserver.core.server.util.JsonResult;

@Controller
public class ValidateAction {

    @RequestMapping("/user")
    public JsonResult user(User user) {
        return JsonResult.ok().put("data", user);
    }

    @RequestMapping("/int")
    public JsonResult ints(Integer i) {
        return JsonResult.ok().put("data", i);
    }

}
