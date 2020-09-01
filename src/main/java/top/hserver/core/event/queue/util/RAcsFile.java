package top.hserver.core.event.queue.util;

import java.io.IOException;
import java.io.RandomAccessFile;


/**
 * @author hxm
 */
public class RAcsFile {
    private RandomAccessFile dataFile;
    private String sFileName;

    public RAcsFile(String sName) throws IOException {
        sFileName = sName;
        String flag = "../";
        if (sFileName.contains(flag)) {
            throw new IOException("路径不规范");
        }
        dataFile = new RandomAccessFile(sFileName, "rw");
    }

    public RAcsFile(String sName, String modeParm) throws IOException {
        sFileName = sName;
        String flag = "../";
        if (sFileName.contains(flag)) {
            throw new IOException("路径不规范");
        }
        dataFile = new RandomAccessFile(sFileName, modeParm);
    }

    public void setLength(int fileLengthParm) throws Exception {
        dataFile.setLength(fileLengthParm);
    }

    public void writeObject(byte[] valueParm) throws IOException {
        dataFile.seek(dataFile.length());
        dataFile.write(valueParm);
    }

    public void writeBytes(byte[] valueParm, int offsetParm, int lengthParm) throws IOException {
        dataFile.seek(dataFile.length());
        dataFile.write(valueParm, offsetParm, lengthParm);
    }

    public void close() throws IOException {
        dataFile.close();
    }

    public void destroy() throws IOException {
        dataFile.close();
        dataFile = null;
        sFileName = null;
    }

    public RandomAccessFile getDataFile() {
        return dataFile;
    }

    public long getFileLength() throws IOException {
        return dataFile.length();
    }
}
