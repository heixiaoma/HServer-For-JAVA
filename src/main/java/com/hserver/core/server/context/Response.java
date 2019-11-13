package com.hserver.core.server.context;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Response {

    private Map<String, String> headers = new HashMap<>();

    private File file;

    private InputStream inputStream;

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
    }

    /**
     * 下载文件啦
     *
     * @param inputStream
     */
    public void setDownloadFile(InputStream inputStream) {
        this.inputStream = inputStream;
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
}
