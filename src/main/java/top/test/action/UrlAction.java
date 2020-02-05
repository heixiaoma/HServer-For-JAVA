package top.test.action;

import top.hserver.core.interfaces.HttpRequest;
import top.hserver.core.ioc.annotation.*;

@Controller
public class UrlAction {

    @GET("/url1/{url}")
    public String url(HttpRequest httpRequest){
        String url = httpRequest.query("url");
        System.out.println(url);
        return url;
    }

    @Sign("签名验证")
    @RequiresRoles("角色")
    @RequiresPermissions(value = {"/权限1","/权限2"}, logical=Logical.OR)
    @GET("/url/{url}")
    public String url(String url){
        return "匹配到的URL:"+url;
    }

    @GET("/a/{url}/bb")
    public String ab(String url){
        return "匹配到的URL:"+url;
    }

    @POST("/post/{url}")
    public String post(String url){
        return "匹配到的URL:"+url;
    }


}
