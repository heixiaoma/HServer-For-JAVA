package net.hserver.action;

import net.hserver.bean.Message;
import top.hserver.core.interfaces.HttpRequest;
import top.hserver.core.ioc.annotation.Controller;
import top.hserver.core.ioc.annotation.POST;
import top.hserver.core.server.util.JsonResult;

import java.util.List;

@Controller
public class JsonCheckAction {

    @POST("/json-post")
    public JsonResult aa(List<Message> messages, HttpRequest request){
        System.out.println(messages.get(0).getName());
        System.out.println(messages);
        return JsonResult.ok();
    }

}
