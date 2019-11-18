package com.test.action;

import com.hserver.core.interfaces.HttpRequest;
import com.hserver.core.interfaces.HttpResponse;
import com.hserver.core.ioc.annotation.Autowired;
import com.hserver.core.ioc.annotation.Controller;
import com.hserver.core.ioc.annotation.GET;
import com.hserver.core.ioc.annotation.POST;
import com.hserver.core.server.context.Request;
import com.hserver.core.server.context.Response;
import com.hserver.core.server.handlers.FileItem;
import com.test.bean.User;
import com.test.service.Test;
import javassist.CtField;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Controller
public class Hello {

    @Autowired
    private Test test1q;

    /**
     * json测试，依赖注入测试
     *
     * @param request
     * @param name
     * @return
     */
    @GET("/hello")
    public Map index(HttpRequest request, String name) {
        Map<String, Object> res = new HashMap<>();
        res.put("code", 200);
        res.put("res", request.getRequestParams());
        res.put("msg", test1q.show("xx"));
        res.put("name", name);
        return res;
    }

    /**
     * 上传文件测试
     *
     * @param request
     * @return
     */
    @POST("/file")
    public Map file(HttpRequest request) {
        Map<String, FileItem> fileItems = request.getFileItems();
        fileItems.forEach((k, v) -> {
            System.out.println(k);
            System.out.println(v);
        });
        Map<String, Object> res = new HashMap<>();
        res.put("code", 200);
        res.put("res", request.getRequestParams());
        res.put("msg", test1q.show("xx"));
        return res;
    }


    /**
     * POST带参数据测试
     *
     * @param request
     * @param a
     * @return
     */
    @POST("/a")
    public Map a(HttpRequest request, Integer a) {
        System.out.println(request.getRequestParams());
        Map<String, Object> res = new HashMap<>();
        res.put("code", 200);
        res.put("res", request.getRequestParams());
        res.put("msg", test1q.show("xx"));
        return res;
    }

    /**
     * 响应头测试
     *
     * @param response
     * @return
     */
    @GET("/head")
    public Map head(HttpRequest request, Response response) {
        Map<String, Object> res = new HashMap<>();
        res.put("code", 200);
        res.put("msg", test1q.show("xx"));
        return res;
    }

    /**
     * 响应头测试
     *
     * @param response
     * @return
     */
    @GET("/down")
    public Map downFile(HttpRequest request, HttpResponse response) {
        response.setDownloadFile(new File("D:\\Java\\HServer\\README.md"));
        Map<String, Object> res = new HashMap<>();
        res.put("code", 200);
        res.put("msg", test1q.show("xx"));
        return res;
    }

    /**
     * javaBean和基础数据类型测试
     *
     * @return
     */
    @GET("/javaBean")
    public Map javaBean(User user, String name) {
        Map<String, Object> res = new HashMap<>();
        res.put("code", 200);
        res.put("msg", test1q.show("xx"));
        res.put("user", user);
        res.put("name", name);
        return res;
    }


    /**
     * httpResponse测试
     *
     * @return
     */
    @GET("/httpResponse")
    public void httpResponse(HttpResponse httpResponse) {
        User user = new User();
        user.setAge(20);
        user.setName("xx");
        user.setSex("男");
        httpResponse.sendJson(user);
//        httpResponse.sendHtml("<h1>666</h1>");
    }


    /**
     * Filter测试
     *
     */
    @GET("/filter")
    public void Filter() {
        //        拦截器会拦截的
    }

    @GET("/template")
    public void template(HttpResponse httpResponse) {
        User user = new User();
        user.setAge(20);
        user.setName("xx");
        user.setSex("男");
        Map<String,Object> obj=new HashMap<>();
        obj.put("user",user);
        httpResponse.sendTemplate("a.ftl", obj);
    }

}
