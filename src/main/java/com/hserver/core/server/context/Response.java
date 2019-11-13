package com.hserver.core.server.context;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Response {

    private Map<String, String> headers = new HashMap<>();

    private File file;

    private InputStream inputStream;

    private boolean isDownload;

    private String fileName;

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
}
