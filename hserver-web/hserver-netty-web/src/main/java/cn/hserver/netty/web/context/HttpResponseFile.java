package cn.hserver.netty.web.context;

import java.io.File;
import java.io.InputStream;

public class HttpResponseFile {
    //下载的文件
    private final File file;

    //下载的文件流
    private final InputStream inputStream;

    //文件名
    private final String fileName;

    private final boolean chunked;

    private final boolean supportContinue;

    private final byte[] content;


    public HttpResponseFile(byte[] content,File file, InputStream inputStream, String fileName, boolean chunked, boolean supportContinue) {
        this.file = file;
        this.content = content;
        this.inputStream = inputStream;
        this.fileName = fileName;
        this.chunked = chunked;
        this.supportContinue = supportContinue;
    }

    public File getFile() {
        return file;
    }

    public byte[] getContent() {
        return content;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public String getFileName() {
        return fileName;
    }

    public boolean isChunked() {
        return chunked;
    }

    public boolean isSupportContinue() {
        return supportContinue;
    }
}
