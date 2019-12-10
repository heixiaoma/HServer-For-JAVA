package top.hserver.core.server.context;

import com.alibaba.fastjson.JSON;
import top.hserver.core.interfaces.HttpResponse;
import top.hserver.core.server.util.FreemarkerUtil;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Response implements HttpResponse {

    private Map<String, String> headers = new ConcurrentHashMap<>();

    private File file;

    private InputStream inputStream;

    private boolean isDownload;

    private String fileName;

    private String jsonAndHtml = null;

    /**
     * 设置响应头
     *
     * @param key
     * @param value
     */
    public void setHeader(String key, String value) {
        this.headers.put(key, value);
    }

    /**
     * 下载文件
     *
     * @param file
     */
    public void setDownloadFile(File file) {
        this.file = file;
        this.isDownload = true;
        this.fileName = file.getName();
    }

    /**
     * 下载文件啦
     *
     * @param inputStream
     */
    public void setDownloadFile(InputStream inputStream, String fileName) {
        this.inputStream = inputStream;
        this.isDownload = true;
        this.fileName = fileName;
    }

    @Override
    public void sendJson(Object object) {
        this.jsonAndHtml = JSON.toJSONString(object);
        headers.put("content-type", "application/json;charset=UTF-8");
    }

    @Override
    public void sendHtml(String html) {
        this.jsonAndHtml = html;
        headers.put("content-type", "text/html;charset=UTF-8");
    }

    @Override
    public void sendTemplate(String htmlPath, Map<String, Object> obj) {
        try {
            this.jsonAndHtml = FreemarkerUtil.getTemplate(htmlPath, obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        headers.put("content-type", "text/html;charset=UTF-8");
    }

    //添加Cookie
    @Override
    public void addCookie(Cookie cookie) {
        Iterator<String> iterator = cookie.keySet().iterator();
        StringBuilder cookieStr = new StringBuilder();
        while (iterator.hasNext()) {
            String k = iterator.next();
            String v = cookie.get(k);
            try {
                cookieStr.append(java.net.URLEncoder.encode(k, "UTF-8") + "=" + java.net.URLEncoder.encode(v, "UTF-8") + ";");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        if (cookie.getMaxAge() != null) {
            cookieStr.append("Max-Age=");
            cookieStr.append(cookie.getMaxAge());
            cookieStr.append(";");
        }
        if (cookie.getPath() != null) {
            cookieStr.append("path=");
            cookieStr.append(cookie.getPath());
            cookieStr.append(";");
        }
        headers.put("Set-Cookie", cookieStr.toString());
    }

    @Override
    public void redirect(String url) {
        headers.put("location", url);
    }


    //---------------系统用的Get操作

    public Map<String, String> getHeaders() {
        return headers;
    }

    public File getFile() {
        return file;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public boolean isDownload() {
        return isDownload;
    }

    public String getFileName() {
        return fileName;
    }

    public String getJsonAndHtml() {
        return jsonAndHtml;
    }
}
