package net.hserver.controller;

import net.hserver.service.UserService;
import top.hserver.core.ioc.annotation.Controller;
import top.hserver.core.ioc.annotation.GET;
import top.hserver.core.ioc.annotation.Resource;
import top.hserver.core.server.util.JsonResult;

@Controller
public class UserController {

    @Resource(serverName = "provider")
    private UserService userService;

    @GET("/userInfo")
    public JsonResult getUserInfo() {
        try {
            String userInfo = userService.getUserInfo();
            return JsonResult.ok(userInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JsonResult.ok();
    }

}
