package com.test.action;

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
    public Map index(Request request, String name) {
        Map<String, Object> res = new HashMap<>();
        res.put("code", 200);
        res.put("res", request.getRequestParams());
        res.put("msg", test1q.show());
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
    public Map file(Request request) {
        Map<String, FileItem> fileItems = request.getFileItems();
        fileItems.forEach((k, v) -> {
            System.out.println(k);
            System.out.println(v);
        });
        Map<String, Object> res = new HashMap<>();
        res.put("code", 200);
        res.put("res", request.getRequestParams());
        res.put("msg", test1q.show());
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
    public Map a(Request request, Integer a) {
        System.out.println(request.getRequestParams());
        Map<String, Object> res = new HashMap<>();
        res.put("code", 200);
        res.put("res", request.getRequestParams());
        res.put("msg", test1q.show());
        return res;
    }

    /**
     * 响应头测试
     *
     * @param response
     * @return
     */
    @GET("/head")
    public Map head(Request request, Response response) {
        response.setHeader("我", "b");
        Map<String, Object> res = new HashMap<>();
        res.put("code", 200);
        res.put("msg", test1q.show());
        return res;
    }

    /**
     * 响应头测试
     *
     * @param response
     * @return
     */
    @GET("/down")
    public Map downFile(Request request, Response response) {
        response.setDownloadFile(new File("D:\\Java\\HServer\\README.MD"));
        Map<String, Object> res = new HashMap<>();
        res.put("code", 200);
        res.put("msg", test1q.show());
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
        res.put("msg", test1q.show());
        res.put("user", user);
        res.put("name", name);
        return res;
    }

}
