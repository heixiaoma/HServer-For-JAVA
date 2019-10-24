package com.hserver.core.server.context;

import java.io.InputStream;

public class StaticFile {

    //文件大小
    private long size;
    //文件名字
    private String fileName;
    //文件类型
    private boolean fileType;
    //文件流
    private InputStream inputStream;


    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isFileType() {
        return fileType;
    }

    public void setFileType(boolean fileType) {
        this.fileType = fileType;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }
}

