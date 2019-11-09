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
    private String fileHeadType;
    //文件流
    private ByteBuf byteBuf;
    //不是下载类型
    private static List<String> readType = new ArrayList<>();
    //该文件的下载类型
    private static List<String> downLoadType = new ArrayList<>();
    //图片类型
    private static List<String> imgType = new ArrayList<>();

    static {
        //文本类型
        readType.add("html");
        readType.add("txt");
        readType.add("xml");
        readType.add("java");
        readType.add("php");
        readType.add("c");
        readType.add("js");
        readType.add("css");

        //文本头
        downLoadType.add("html");
        downLoadType.add("txt");
        downLoadType.add("xml");
        downLoadType.add("js");
        downLoadType.add("css");

        imgType.add("png");
        imgType.add("jpg");
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

    public String getFileHead() {
        if (this.fileType && fileHeadType != null) {
            return this.fileHeadType;
        } else {
            return "text/plain";
        }
    }

    public void setFileType(String type) {

        if (readType.contains(type)) {
            this.fileType = true;
            if (downLoadType.contains(type)) {
                this.fileHeadType = "text/" + type;
            }
            if (imgType.contains("type")) {
                this.fileHeadType = "image/" + type;
            }

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

