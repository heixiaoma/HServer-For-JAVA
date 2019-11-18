package com.hserver.core.server.context;

import com.alibaba.fastjson.JSON;
import com.hserver.core.interfaces.HttpResponse;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Response implements HttpResponse {

    private Map<String, String> headers = new HashMap<>();

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
    public void sendTemplate(String htmlPath, Map<String,Object> obj) {

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
