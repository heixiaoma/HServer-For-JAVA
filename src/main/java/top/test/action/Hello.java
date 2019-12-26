package top.test.action;

import top.hserver.core.interfaces.HttpRequest;
import top.hserver.core.interfaces.HttpResponse;
import top.hserver.core.ioc.annotation.Autowired;
import top.hserver.core.ioc.annotation.Controller;
import top.hserver.core.ioc.annotation.GET;
import top.hserver.core.ioc.annotation.POST;
import top.hserver.core.server.context.Cookie;
import top.hserver.core.server.context.Response;
import top.hserver.core.server.handlers.FileItem;
import top.hserver.core.server.handlers.StatisticsHandler;
import top.hserver.core.server.stat.IpData;
import top.test.bean.User;
import top.test.service.Test;
import top.test.service.UserService;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Controller
public class Hello {

    @Autowired
    private Test test1q;


    @Autowired
    private UserService userService;


    @GET("/")
    public void index(HttpResponse httpResponse) {
        httpResponse.redirect("/hserver.html");
    }


    /**
     * json测试，依赖注入测试
     *
     * @param request
     * @param name
     * @return
     */
    @GET("/hello")
    public Map hello(HttpRequest request, String name) {
        Map<String, Object> res = new HashMap<>();
        res.put("code", 200);
        res.put("res", request.getRequestParams());
        res.put("msg", test1q.show("xx"));
        res.put("name", name);
        return res;
    }


    @GET("/stat")
    public Map stat(String name) {
        Map<String, Object> res = new HashMap<>();
        res.put("code", 200);
        res.put("name", name);
        res.put("ipMap",StatisticsHandler.getIpMap());
        res.put("logRequestQue",StatisticsHandler.getLogRequestQue());
        res.put("uniqueIpCount", StatisticsHandler.getUniqueIpCount());
        res.put("count", StatisticsHandler.getCount());
        res.put("uriData", StatisticsHandler.getUriData());
        return res;
    }

    @GET("/removeStat")
    public Map removeStat() {
        Map<String, Object> res = new HashMap<>();
        res.put("code", 200);
        res.put("ipMap",StatisticsHandler.removeIpMap());
        res.put("logRequestQue",StatisticsHandler.removeLogRequestQue());
        res.put("uniqueIpCount", StatisticsHandler.removeUniqueIpCount());
        res.put("count", StatisticsHandler.removeCount());
        res.put("uriData", StatisticsHandler.removeUriData());
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
    @GET("/downFile")
    public void downFile(HttpRequest request, HttpResponse response) {
        response.setDownloadFile(new File("D:\\Java\\HServer\\README.md"));
    }

    @GET("/downInputStream")
    public void downInputStream(HttpRequest request, HttpResponse response) throws Exception {
        File file = new File("D:\\Java\\HServer\\README.md");
        InputStream fileInputStream = new FileInputStream(file);
        response.setDownloadFile(fileInputStream,"README.md");
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

    /**
     * 模板测试
     * @param httpResponse
     */
    @GET("/template")
    public void template(HttpResponse httpResponse) {
        User user = new User();
        user.setAge(20);
        user.setName("xx");
        user.setSex("男");
        Map<String,Object> obj=new HashMap<>();
        obj.put("user",user);
//        httpResponse.sendTemplate("/admin/user/list.ftl", obj);
        httpResponse.sendTemplate("a.ftl", obj);
    }

    @GET("/headers")
    public Map<String, String> headers(HttpRequest request,HttpResponse httpResponse) {
//        httpResponse.setHeader("Set-Cookie","token=cowshield");
        Cookie cookie=new Cookie().add("name","张三").add("age","20");
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
        return (1/0)+"x";
    }



    @GET("/config")
    public Map<String,Object> config(){
        Map<String,Object> obj=new HashMap<>();
        obj.put("user1",userService.getUser1());
        obj.put("user2",userService.getUser2());
        return obj;
    }

}
