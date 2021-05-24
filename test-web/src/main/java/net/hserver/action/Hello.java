package net.hserver.action;

import net.hserver.bean.User;
import net.hserver.log.Log;
import net.hserver.service.HelloService;
import net.hserver.service.TService;
import net.hserver.service.Test;
import net.hserver.service.UserService;
import top.hserver.core.interfaces.HttpRequest;
import top.hserver.core.interfaces.HttpResponse;
import top.hserver.core.ioc.annotation.*;
import top.hserver.core.server.context.Cookie;
import top.hserver.core.server.context.PartFile;
import top.hserver.core.server.context.Response;
import top.hserver.core.server.util.JsonResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class Hello {

    @Autowired
    private Test test1q;

    @Autowired
    private UserService userService;

    @Autowired
    private HelloService helloService;

    @Autowired("t1")
    private TService tService;

    @Autowired("t2")
    private TService tService2;

    @GET("/")
    public void index(HttpResponse httpResponse) {
        httpResponse.redirect("/hserver.html");
    }


    @GET("/sayHello")
    public String sayHello() {
        return helloService.sayHello();
    }

    /**
     * json测试，依赖注入测试
     *
     * @param request
     * @param name
     * @return
     */
    @GET("/hello")
    @Log
    @Track
    public Map hello(HttpRequest request, String name) {

        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Map<String, Object> res = new HashMap<>();
        res.put("code", 200);
        res.put("res", request.getRequestParams());
        res.put("msg", test1q.show("xx"));
        res.put("name", name);
        return res;
    }

    @Track
    @GET("/t")
    public JsonResult r() {
        return JsonResult.ok(tService.t() + tService2.t());
    }

    @GET("/test1")
    public JsonResult test() {
        return JsonResult.ok();
    }

    @GET("/test2")
    public JsonResult test2() {

        try {
            Thread.sleep(1000);
        } catch (Exception e) {
        }
        return JsonResult.ok();
    }


    @Track
    @GET("/track")
    public JsonResult track() {
        return JsonResult.ok();
    }


    /**
     * 上传文件测试
     *
     * @param request
     * @return
     */
    @POST("/file")
    public Map file(HttpRequest request) {

        Map<String, PartFile> fileItems = request.getMultipartFile();
        fileItems.forEach((k, v) -> {
            System.out.println(k);
            System.out.println(v);
            byte[] data = v.getData();
            System.out.println(data);
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

    @POST("/b")
    public Map b(HttpRequest request) {
        return JsonResult.ok();
    }

    @POST("/raw")
    public Map raw(User user) {
        return JsonResult.ok().put("data", user);
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
    @GET("/downFile")
    public void downFile(HttpRequest request, HttpResponse response) {
        response.setDownloadFile(new File("D:\\Java\\HServer\\README.md"));
    }

    @GET("/downInputStream")
    public void downInputStream(HttpRequest request, HttpResponse response) throws Exception {
        File file = new File("D:\\Java\\HServer\\README.md");
        InputStream fileInputStream = new FileInputStream(file);
        response.setDownloadFile(fileInputStream, "README.md");
    }

    @GET("/null")
    public void nullMethod(HttpRequest request, HttpResponse response) throws Exception {

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
     */
    @GET("/filter")
    public void Filter() {
        //        拦截器会拦截的
    }

    /**
     * 模板测试
     *
     * @param httpResponse
     */
    @GET("/template")
    public void template(HttpResponse httpResponse) {
        User user = new User();
        user.setAge(20);
        user.setName("xx");
        user.setSex("男");
        Map<String, Object> obj = new HashMap<>();
        obj.put("user2", user);
        List<String> lists = new ArrayList<>();
        lists.add("1");
        lists.add("2");
        lists.add("3");
        lists.add("4");
        obj.put("list", lists);
//        httpResponse.sendTemplate("/admin/user/list.ftl", obj);
        httpResponse.sendTemplate("a.ftl", obj);
    }

    @GET("/headers")
    public Map<String, String> headers(HttpRequest request, HttpResponse httpResponse) {
//        httpResponse.setHeader("Set-Cookie","token=cowshield");
        Cookie cookie = new Cookie().add("name", "张三").add("age", "20");
        cookie.setPath("/");
        cookie.setMaxAge(20);
        httpResponse.addCookie(cookie);
        return request.getHeaders();
    }


    @GET("/redirect")
    public void redirect(HttpResponse httpResponse) {
        httpResponse.redirect("http://baidu.com");
    }

    @GET("/error")
    public String error(HttpResponse httpResponse) {
        return (1 / 0) + "x";
    }


    @GET("/config")
    public Map<String, Object> config() {
        Map<String, Object> obj = new HashMap<>();
        obj.put("user1", userService.getUser1());
        obj.put("user2", userService.getUser2());
        return obj;
    }

    @GET("/par")
    public JsonResult par(String name, int info) {
        return JsonResult.ok();
    }

}
