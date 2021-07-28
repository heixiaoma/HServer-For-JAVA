package net.hserver.action;

import net.hserver.bean.User;
import top.hserver.core.ioc.annotation.Controller;
import top.hserver.core.ioc.annotation.POST;
import top.hserver.core.server.util.JsonResult;

import java.util.List;
import java.util.Set;

@Controller
public class Json {

    @POST("/set")
    public JsonResult set(Set<User> users){
        for (User user : users) {
            System.out.println("--------"+user);
        }
        return JsonResult.ok().put("data",users);
    }


    @POST("/list")
    public JsonResult list(List<User> users){
        for (User user : users) {
            System.out.println("--------"+user);
        }
        return JsonResult.ok().put("data",users);
    }

}
