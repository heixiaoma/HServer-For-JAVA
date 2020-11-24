package test8;

import java.io.File;

public class HFile {

    private String key;

    private String fileName;

    private File file;

    private String contentType;

    public HFile(String key, String fileName, File file) {
        this.file = file;
        this.fileName = fileName;
        this.key = key;
    }

    public HFile(String key, String fileName, File file, String contentType) {
        this.file = file;
        this.fileName = fileName;
        this.key = key;
        this.contentType = contentType;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
