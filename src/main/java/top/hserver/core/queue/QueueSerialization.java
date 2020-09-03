package top.hserver.core.queue;

import top.hserver.cloud.util.SerializationUtil;
import top.hserver.core.server.context.ConstConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 我们约定两个文件一个文件存储文件索引，一个文件存储调用数据。
 * 对参数进行序列化
 *
 * @author hxm
 */
public class QueueSerialization {
    private final String queueData = ConstConfig.PATH + "queue" + File.separator + "QueueData.queue";
    private final String queueIndex = ConstConfig.PATH + "queue" + File.separator + "QueueIndex.queue";

    private RandomAccessFile queueDataRandomAccessFile;

    {
        try {
            queueDataRandomAccessFile = new RandomAccessFile(queueData, "rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private RandomAccessFile queueIndexRandomAccessFile;

    {
        try {
            queueIndexRandomAccessFile = new RandomAccessFile(queueIndex, "rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void cacheQueue(byte[] data) throws IOException {
        FileChannel channel = queueDataRandomAccessFile.getChannel();
        long preIndex = channel.size();
        channel.position(preIndex);
        channel.write(ByteBuffer.wrap(data));
        long nowIndex = channel.position();
        System.out.println(nowIndex-preIndex);
    }

    public byte[] fetchQueue() throws Exception{
        FileChannel channel = queueDataRandomAccessFile.getChannel();
        ByteBuffer buff = ByteBuffer.allocate(12);
        channel.read(buff,36);
        buff.flip();
        byte[] bytes=new byte[12];
        buff.get(bytes);
        return bytes;
    }

    public static void main(String[] args) throws Exception {
        QueueSerialization queueSerialization = new QueueSerialization();
        queueSerialization.cacheQueue("牛逼".getBytes());
    }


}
