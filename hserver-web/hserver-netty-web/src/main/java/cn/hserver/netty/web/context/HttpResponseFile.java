package cn.hserver.netty.web.context;

import cn.hserver.mvc.response.ProgressStatus;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class HttpResponseFile {
    //下载的文件
    private final File file;

    //下载的文件流
    private final InputStream inputStream;

    //文件名
    private final String fileName;

    private ProgressStatus progressStatus;

    private boolean bigFile=false;

    public HttpResponseFile(File file, InputStream inputStream, String fileName) {
        this.file = file;
        this.inputStream = inputStream;
        this.fileName = fileName;
    }

    public File getFile() {
        return file;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public String getFileName() {
        return fileName;
    }

    public void setBigFile(boolean bigFile) {
        this.bigFile = bigFile;
    }

    public boolean isBigFile() {
        return bigFile;
    }

    public ProgressStatus getProgressStatus() {
        return progressStatus;
    }

    public void setProgressStatus(ProgressStatus progressStatus) {
        this.progressStatus = progressStatus;
    }
}
