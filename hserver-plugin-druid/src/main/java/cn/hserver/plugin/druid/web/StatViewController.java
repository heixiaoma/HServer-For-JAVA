package cn.hserver.plugin.druid.web;


import cn.hserver.plugin.druid.web.config.HtmlConst;
import cn.hserver.plugin.web.annotation.Controller;
import cn.hserver.plugin.web.annotation.GET;
import cn.hserver.plugin.web.annotation.RequestMapping;
import cn.hserver.plugin.web.interfaces.HttpResponse;
import com.alibaba.druid.stat.DruidStatService;
import com.alibaba.druid.util.Utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Controller
public class StatViewController {

    /**
     * 获取Druid的统计
     */
    private final DruidStatService statService = DruidStatService.getInstance();

    @RequestMapping("/druid/{uri}")
    public void druid(String uri, HttpResponse response) throws IOException {
        String druidPath = "support/http/resources/";
        String url = druidPath + uri;
        if (uri.endsWith("json")) {
            response.sendJsonString(statService.service("/"+uri));
        } else {
            if (url.endsWith(".jpg")) {
                byte[] bytes = Utils.readByteArrayFromResource(url);
                if (bytes != null) {
                    response.setDownloadFile(new ByteArrayInputStream(bytes), url);
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
                response.setHeader("content-type", "text/html; charset=utf-8");
            }
            if (url.endsWith(".css")) {
                response.setHeader("content-type", "text/css;charset=utf-8");
            } else if (url.endsWith(".js")) {
                response.setHeader("content-type", "text/javascript;charset=utf-8");
            }
            response.sendHtml(text);
        }
    }
}
