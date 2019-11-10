package com.test.action;

import com.hserver.core.ioc.annotation.Action;
import com.hserver.core.ioc.annotation.GET;
import com.hserver.core.ioc.annotation.In;
import com.hserver.core.ioc.annotation.POST;
import com.hserver.core.server.context.Request;
import com.hserver.core.server.handlers.FileItem;
import com.test.bean.Test;

import java.util.HashMap;
import java.util.Map;

@Action
public class Hello {

    @In
    private Test test1q;


    @GET("/hello")
    public Map index(Request request, String name) {
        Map<String, Object> res = new HashMap<>();
        res.put("code", 200);
        res.put("res", request.getRequestParams());
        res.put("msg", test1q.show());
        res.put("name", name);
        return res;
    }

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


    @POST("/a")
    public Map a(Request request, Integer a) {
        System.out.println(request.getRequestParams());
        Map<String, Object> res = new HashMap<>();
        res.put("code", 200);
        res.put("res", request.getRequestParams());
        res.put("msg", test1q.show());
        return res;
    }

}
