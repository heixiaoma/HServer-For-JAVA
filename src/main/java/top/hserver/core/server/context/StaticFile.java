package top.hserver.core.server.context;


import java.io.InputStream;

/**
 * @author hxm
 */
public class StaticFile {

    /**
     * 文件大小
     */
    private long size;
    /**
     * 文件名字
     */
    private String fileName;
    /**
     * 文件后缀
     */
    private String fileSuffix;
    /**
     * 文件流
     */
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

    public String getFileHead() {
        return MimeType.get(fileSuffix);
    }

    public void setFileType(String type) {
        this.fileSuffix = type;
    }

  public InputStream getInputStream() {
    return inputStream;
  }

  public void setInputStream(InputStream inputStream) {
    this.inputStream = inputStream;
  }
}

