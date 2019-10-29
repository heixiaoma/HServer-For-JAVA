package com.hserver.core.server.context;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;


public class StaticFile {

    //文件大小
    private long size;
    //文件名字
    private String fileName;
    //是否文件类型
    private boolean fileType;
    //文件流
    private ByteBuf byteBuf;

    private static List<String> readType = new ArrayList<>();

    static {
        readType.add("html");
        readType.add("txt");
        readType.add("xml");
        readType.add("java");
        readType.add("php");
        readType.add("c");
        readType.add("js");
        readType.add("css");
    }


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

    public void setFileType(String type) {

        if (readType.contains(type)) {
            this.fileType = true;
        } else {
            this.fileType = false;
        }


    }

    public ByteBuf getByteBuf() {
        return byteBuf;
    }

    public void setByteBuf(ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
    }
}

