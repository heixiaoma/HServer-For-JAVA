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
            int size = 10000;
            long l = System.currentTimeMillis();
            for (int i = 0; i < size; i++) {
                String userInfo = userService.getUserInfo();
            }
            return JsonResult.ok().put("data", size + "次调用耗时：" + (System.currentTimeMillis() - l) / 1000.0 + "/s");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JsonResult.ok();
    }

}
