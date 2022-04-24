package top.hserver.core.queue.fqueue.internal;


import top.hserver.core.queue.fqueue.exception.FileFormatException;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 数据索引文件
 */
public class Index {
    private static final int INDEX_LIMIT_LENGTH = 32;
    private static final String INDEX_FILE_NAME = "fq.idx";

    private RandomAccessFile dbRandFile = null;
    private FileChannel fc;
    private MappedByteBuffer mappedByteBuffer;

    /**
     * 文件操作位置信息
     */
    private String magicString = null;
    private int version = -1;
    private long readerPosition = -1;
    private long writerPosition = -1;
    private long readerIndex = -1;
    private long writerIndex = -1;
    private AtomicLong size = new AtomicLong();

    public Index(String path) throws IOException, FileFormatException {
        File dbFile = new File(path, INDEX_FILE_NAME);

        // 文件不存在，创建文件
        if (dbFile.exists() == false) {
            dbFile.createNewFile();
            dbRandFile = new RandomAccessFile(dbFile, "rwd");
            initIdxFile();
        } else {
            dbRandFile = new RandomAccessFile(dbFile, "rwd");
            if (dbRandFile.length() < INDEX_LIMIT_LENGTH) {
                throw new FileFormatException("file format error.");
            }
            byte[] bytes = new byte[INDEX_LIMIT_LENGTH];
            dbRandFile.read(bytes);
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            bytes = new byte[Entity.MAGIC.getBytes().length];
            buffer.get(bytes);
            magicString = new String(bytes);
            version = buffer.getInt();
            readerPosition = buffer.getInt();
            writerPosition = buffer.getInt();
            readerIndex = buffer.getInt();
            writerIndex = buffer.getInt();
            int sz = buffer.getInt();
            if (readerPosition == writerPosition && readerIndex == writerIndex && sz <= 0) {
                initIdxFile();
            } else {
                size.set(sz);
            }
        }
        fc = dbRandFile.getChannel();
        mappedByteBuffer = fc.map(MapMode.READ_WRITE, 0, INDEX_LIMIT_LENGTH);
    }

    private void initIdxFile() throws IOException {
        magicString = Entity.MAGIC;
        version = 1;
        readerPosition = Entity.MESSAGE_START_POSITION;
        writerPosition = Entity.MESSAGE_START_POSITION;
        readerIndex = 1;
        writerIndex = 1;
        dbRandFile.setLength(32);
        dbRandFile.seek(0);
        dbRandFile.write(magicString.getBytes());// magic
        dbRandFile.writeInt(version);// 8 version
        dbRandFile.writeLong(readerPosition);// 12 reader position
        dbRandFile.writeLong(writerPosition);// 16 write position
        dbRandFile.writeLong(readerIndex);// 20 reader index
        dbRandFile.writeLong(writerIndex);// 24 writer index
        dbRandFile.writeLong(0);// 28 size
    }

    public void clear() throws IOException {
        mappedByteBuffer.clear();
        mappedByteBuffer.force();
        initIdxFile();
    }

    /**
     * 记录写位置
     *
     * @param pos
     */
    public void putWriterPosition(long pos) {
        mappedByteBuffer.position(16);
        mappedByteBuffer.putLong(pos);
        this.writerPosition = pos;
    }

    /**
     * 记录读取的位置
     *
     * @param pos
     */
    public void putReaderPosition(long pos) {
        mappedByteBuffer.position(12);
        mappedByteBuffer.putLong(pos);
        this.readerPosition = pos;
    }

    /**
     * 记录写文件索引
     *
     * @param index
     */
    public void putWriterIndex(long index) {
        mappedByteBuffer.position(24);
        mappedByteBuffer.putLong(index);
        this.writerIndex = index;
    }

    /**
     * 记录读取文件索引
     *
     * @param index
     */
    public void putReaderIndex(long index) {
        mappedByteBuffer.position(20);
        mappedByteBuffer.putLong(index);
        this.readerIndex = index;
    }

    public void incrementSize() {
        long num = size.incrementAndGet();
        mappedByteBuffer.position(28);
        mappedByteBuffer.putLong(num);
    }

    public void decrementSize() {
        long num = size.decrementAndGet();
        mappedByteBuffer.position(28);
        mappedByteBuffer.putLong(num);
    }

    public String getMagicString() {
        return magicString;
    }

    public int getVersion() {
        return version;
    }

    public long getReaderPosition() {
        return readerPosition;
    }

    public long getWriterPosition() {
        return writerPosition;
    }

    public long getReaderIndex() {
        return readerIndex;
    }

    public long getWriterIndex() {
        return writerIndex;
    }

    public long getSize() {
        return size.get();
    }

    /**
     * 关闭索引文件
     */
    public void close() throws IOException {
        mappedByteBuffer.force();
        mappedByteBuffer.clear();
        fc.close();
        dbRandFile.close();
        mappedByteBuffer = null;
        fc = null;
        dbRandFile = null;
    }

    public String headerInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append(" magicString:");
        sb.append(magicString);
        sb.append(" version:");
        sb.append(version);
        sb.append(" readerPosition:");
        sb.append(readerPosition);
        sb.append(" writerPosition:");
        sb.append(writerPosition);
        sb.append(" size:");
        sb.append(size);
        sb.append(" readerIndex:");
        sb.append(readerIndex);
        sb.append(" writerIndex:");
        sb.append(writerIndex);
        return sb.toString();
    }

}
