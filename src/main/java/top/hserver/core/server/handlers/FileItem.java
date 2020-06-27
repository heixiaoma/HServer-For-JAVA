package top.hserver.core.server.handlers;


import lombok.Data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


/**
 * @author hxm
 */
@Data
public class FileItem {

    /**
     * 注意如果你是通过File 去炒作的文件，请自己删除临时文件哦，不然会垃圾文件很多
     * moveTo();
     * getData();
     */

    /**
     * 上传的表单字段
     */
    private String formName;

    /**
     * 上传的文件名
     */
    private String fileName;

    /**
     * File temp path
     */
    private String filePath;

    /**
     * File Content Type
     */
    private String contentType;

    /**
     * File size, unit: byte
     */
    private long length;

    private File file;

    /**
     * 扩展名字
     *
     * @return
     */
    public String extName() {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    @Override
    public String toString() {
        long kb = length / 1024;
        return "FileItem(" +
                "name='" + formName + '\'' +
                "fileName='" + fileName + '\'' +
                ", path='" + filePath + '\'' +
                ", contentType='" + contentType + '\'' +
                ", size=" + (kb < 1 ? 1 : kb) + "KB)";
    }

    public void moveTo(File newFile) throws IOException {
        this.moveTo(Paths.get(newFile.getPath()));
    }

    private void moveTo(Path newFile) throws IOException {
        Files.move(Paths.get(file.getPath()), newFile, StandardCopyOption.REPLACE_EXISTING);
    }

    public byte[] getData() {
        byte[] fileContent;
        try {
            fileContent = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            file.delete();
        }
        return fileContent;
    }

    public FileInputStream getFileInputStream() {
        try {
            return new FileInputStream(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            file.delete();
        }
    }

    public String getFileToString() {
        try {
            return new String(Files.readAllBytes(file.toPath()), "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            file.delete();
        }
    }

}