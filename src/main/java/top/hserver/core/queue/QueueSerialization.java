package top.hserver.core.queue;

import top.hserver.cloud.util.SerializationUtil;
import top.hserver.core.server.context.ConstConfig;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 我们约定两个文件一个文件存储文件索引，一个文件存储调用数据。
 * 对参数进行序列化
 * <p>
 * 队列数据存储规则
 * queueData[长度+内容 长度+内容 长度+内容 ....]
 *
 * @author hxm
 */
public class QueueSerialization {

    public static final String queuePath = ConstConfig.PATH + "queue" + File.separator;

    private final String queueIndex = queuePath + "queueBlockIndex.queue";

    private final static Map<String, RandomAccessFile> RANDOM_ACCESS_FILE_MAP = new ConcurrentHashMap<>();

    private static RandomAccessFile getRandomAccessFile(String path, String mode) {
        try {
            if (mode == null) {
                RandomAccessFile randomAccessFile = RANDOM_ACCESS_FILE_MAP.get(path);
                randomAccessFile.close();
                RANDOM_ACCESS_FILE_MAP.remove(path);
                return null;
            }
            RandomAccessFile randomAccessFile = RANDOM_ACCESS_FILE_MAP.get(path);
            if (randomAccessFile == null) {
                RandomAccessFile rd = new RandomAccessFile(path, mode);
                RANDOM_ACCESS_FILE_MAP.put(path, rd);
                return rd;
            }
            return randomAccessFile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 一个Block缓存1万个消息
     */
    private final long QUEUE_SIZE = 10000;

    private RandomAccessFile queueIndexRandomAccessFile = getRandomAccessFile(queueIndex, "rw");

    /**
     * long数组转成byte
     *
     * @param number
     * @return
     */
    public static byte[] longToByte(long number) {
        long temp = number;
        byte[] b = new byte[8];
        for (int i = 0; i < b.length; i++) {
            b[i] = new Long(temp & 0xff).byteValue();
            temp = temp >> 8;
        }
        return b;
    }

    /**
     * byte数组转成long
     *
     * @param b
     * @return
     */
    public static long byteToLong(byte[] b) {
        long s = 0;
        long s0 = b[0] & 0xff;
        long s1 = b[1] & 0xff;
        long s2 = b[2] & 0xff;
        long s3 = b[3] & 0xff;
        long s4 = b[4] & 0xff;
        long s5 = b[5] & 0xff;
        long s6 = b[6] & 0xff;
        long s7 = b[7] & 0xff;

        // s0不变
        s1 <<= 8;
        s2 <<= 16;
        s3 <<= 24;
        s4 <<= 8 * 4;
        s5 <<= 8 * 5;
        s6 <<= 8 * 6;
        s7 <<= 8 * 7;
        s = s0 | s1 | s2 | s3 | s4 | s5 | s6 | s7;
        return s;
    }


    /**
     * 缓存一个Queue
     *
     * @param data
     * @throws IOException
     */
    public synchronized void cacheQueue(byte[] data) throws IOException {

        //添加描述信息
        QueueBlockIndex queueBlockIndex;

        FileChannel queueIndexRandomAccessFileChannel = queueIndexRandomAccessFile.getChannel();
        //如果为0，那么是第一使用，那么就创建一个进去
        String queueData = "-queueData.queue";
        if (queueIndexRandomAccessFileChannel.size() == 0) {
            queueBlockIndex = new QueueBlockIndex();
            //定义一个Queue名字
            String name = System.currentTimeMillis() + queueData;
            queueBlockIndex.setUseBlockName(name);
            queueBlockIndex.addBlockName(name);
        } else {
            //读取描述信息
            int i = new Long(queueIndexRandomAccessFileChannel.size()).intValue();
            ByteBuffer buff = ByteBuffer.allocate(i);
            queueIndexRandomAccessFileChannel.position(0);
            queueIndexRandomAccessFileChannel.read(buff);
            buff.flip();
            byte[] bytes = new byte[i];
            buff.get(bytes);
            queueBlockIndex = SerializationUtil.deserialize(bytes, QueueBlockIndex.class);
        }
        //检查Block大小
        int lastQueueBlockSize = queueBlockIndex.getLastQueueBlockSize();
        if (lastQueueBlockSize == -1) {
            queueBlockIndex = new QueueBlockIndex();
            //定义一个Queue名字
            String name = System.currentTimeMillis() + queueData;
            queueBlockIndex.addBlockName(name);
        } else if (QUEUE_SIZE <= lastQueueBlockSize) {
            //重新设置新的块
            //定义一个Queue名字
            String name = System.currentTimeMillis() + queueData;
            queueBlockIndex.addBlockName(name);
        }
        //块大小加1
        queueBlockIndex.incrementIndex();

        //写一个数据到块里
        RandomAccessFile randomAccessFile = getRandomAccessFile(queuePath + queueBlockIndex.getLastQueueBlockName(), "rw");
        FileChannel queueDataRandomAccessFileChannel = randomAccessFile.getChannel();
        /**
         * 数据格式 8字节 + queue内容  8字节存内容长度 Long类型住够了
         */
        byte[] bytes = longToByte(data.length);
        byte[] newByte = new byte[8 + data.length];
        //copy 长度 到新的字节数组里
        System.arraycopy(bytes, 0, newByte, 0, bytes.length);
        //copy 数据到新的字节数组里
        System.arraycopy(data, 0, newByte, bytes.length, data.length);
        queueDataRandomAccessFileChannel.position(queueDataRandomAccessFileChannel.size());
        queueDataRandomAccessFileChannel.write(ByteBuffer.wrap(newByte));
        //写Block信息到文件
        queueIndexRandomAccessFileChannel.truncate(0);
        queueIndexRandomAccessFileChannel.write(ByteBuffer.wrap(SerializationUtil.serialize(queueBlockIndex)));
    }

    /**
     * 获得一批Queue
     *
     * @return
     * @throws Exception
     */
    public synchronized byte[] fetchQueue() throws Exception {
        FileChannel queueIndexRandomAccessFileChannel = queueIndexRandomAccessFile.getChannel();
        int i = new Long(queueIndexRandomAccessFileChannel.size()).intValue();
        ByteBuffer buffData = ByteBuffer.allocate(i);
        queueIndexRandomAccessFileChannel.position(0);
        queueIndexRandomAccessFileChannel.read(buffData);
        buffData.flip();
        byte[] byteData = new byte[i];
        buffData.get(byteData);
        QueueBlockIndex queueBlockIndex = SerializationUtil.deserialize(byteData, QueueBlockIndex.class);

        RandomAccessFile randomAccessFile = getRandomAccessFile(queuePath + queueBlockIndex.getUseBlockName(), "r");

        //判断是否存在未消费持久队列
        if (queueBlockIndex.getPosition() >= randomAccessFile.length()) {
            //当前队列消费完了，在变更到下一个文件进行消费；
            getRandomAccessFile(queuePath + queueBlockIndex.getUseBlockName(), null);
            if (queueBlockIndex.changeNext()) {
                randomAccessFile = getRandomAccessFile(queuePath + queueBlockIndex.getUseBlockName(), "r");
            } else {
                queueIndexRandomAccessFileChannel.truncate(0);
                throw new NullPointerException("完事");
            }
        }
        FileChannel channel = randomAccessFile.getChannel();
        channel.position(queueBlockIndex.getPosition());
        //读取 内容长度
        ByteBuffer contentLength = ByteBuffer.allocate(8);
        channel.read(contentLength);
        contentLength.flip();
        byte[] contentLengthByte = new byte[8];
        contentLength.get(contentLengthByte);
        long l = byteToLong(contentLengthByte);
        //读取内容
        int cl = new Long(l).intValue();
        ByteBuffer content = ByteBuffer.allocate(cl);
        channel.position(queueBlockIndex.getPosition() + 8);
        channel.read(content);
        content.flip();
        byte[] contentBytes = new byte[cl];
        content.get(contentBytes);
        //设置当前的所有位置，
        queueBlockIndex.setPosition(queueBlockIndex.getPosition() + 8 + cl);
        queueIndexRandomAccessFileChannel.truncate(0);
        queueIndexRandomAccessFileChannel.write(ByteBuffer.wrap(SerializationUtil.serialize(queueBlockIndex)));
        return contentBytes;
    }

}