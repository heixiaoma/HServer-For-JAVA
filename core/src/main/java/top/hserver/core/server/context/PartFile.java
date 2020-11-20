package top.hserver.core.server.context;


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
public class PartFile {

    /**
     * 注意如果你是通过File 去操作作的文件，请自己删除临时文件哦，不然会垃圾文件很多
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
     * 文件临时目录
     */
    private String filePath;

    /**
     * 文件类型
     */
    private String contentType;

    /**
     * 文件大小字节
     */
    private long length;

    /**
     * 文件对象
     */
    private File file;

    /**
     * 扩展名字
     *
     * @return
     */
    public String extName() {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }


    /**
     * 点扩展名字
     *
     * @return
     */
    public String pointExtName() {
      return fileName.substring(fileName.lastIndexOf("."));
    }


    /**
     * 移动文件
     * @param newFile
     * @throws IOException
     */
    public void moveTo(File newFile) throws IOException {
        this.moveTo(Paths.get(newFile.getPath()));
    }

    /**
     * 移动文件
     * @param newFile
     * @throws IOException
     */
    private void moveTo(Path newFile) throws IOException {
        Files.move(Paths.get(file.getPath()), newFile, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * 文件转字节
     * @return
     */
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

    /**
     * 文件转文件流
     * @return
     */
    public FileInputStream getFileInputStream() {
        try {
            return new FileInputStream(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            file.delete();
        }
    }

    /**
     * 文件转字符串
     * @return
     */
    public String getFileToString() {
        try {
            return new String(Files.readAllBytes(file.toPath()), "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            file.delete();
        }
    }

    /**
     * 删除缓存文件
     */
    public void deleteTempCacheFile(){
        file.delete();
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

}