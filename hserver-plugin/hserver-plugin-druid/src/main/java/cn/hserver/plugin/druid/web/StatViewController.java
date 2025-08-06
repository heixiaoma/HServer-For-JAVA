package cn.hserver.plugin.druid.web;


import cn.hserver.mvc.annotation.Controller;
import cn.hserver.mvc.annotation.router.RequestMapping;
import cn.hserver.mvc.response.Response;
import cn.hserver.plugin.druid.web.config.HtmlConst;
import com.alibaba.druid.stat.DruidStatService;
import com.alibaba.druid.util.Utils;

import java.io.IOException;

@Controller
public class StatViewController {
    /**
     * 获取Druid的统计
     */
    private final DruidStatService statService = DruidStatService.getInstance();

    @RequestMapping("/druid/{uri}")
    public void druid(String uri, Response response) throws IOException {
        String druidPath = "support/http/resources/";
        String url = druidPath + uri;
        if (uri.endsWith("json")) {
            response.sendJsonString(statService.service("/"+uri));
        } else {
            if (url.endsWith(".jpg")) {
                byte[] bytes = Utils.readByteArrayFromResource(url);
                if (bytes != null) {
                    response.downloadBytes(bytes, url);
                }
            }
            String text = Utils.readFromResource(url);
            if (url.contains("header.html")){
                text= HtmlConst.HEADER;
            }
            if (text == null) {
                return;
            }
            if (url.endsWith(".html")) {
                response.addHeader("content-type", "text/html; charset=utf-8");
            }
            if (url.endsWith(".css")) {
                response.addHeader("content-type", "text/css;charset=utf-8");
            } else if (url.endsWith(".js")) {
                response.addHeader("content-type", "text/javascript;charset=utf-8");
            }
            response.sendHtml(text);
        }
    }
}
