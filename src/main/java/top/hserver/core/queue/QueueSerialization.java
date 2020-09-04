package top.hserver.core.queue;

import top.hserver.cloud.util.SerializationUtil;
import top.hserver.core.server.context.ConstConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * 我们约定两个文件一个文件存储文件索引，一个文件存储调用数据。
 * 对参数进行序列化
 *
 * @author hxm
 */
public class QueueSerialization {
    private final String queueData = ConstConfig.PATH + "queue" + File.separator + "QueueData.queue";
    private final String queueIndex = ConstConfig.PATH + "queue" + File.separator + "QueueIndex.queue";
    //索引字节大小
    private final int INDEX_SIZE = 10;
    //上次读取的大小位置
    private int markIndex = 0;

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

    /**
     * 缓存一个Queue
     *
     * @param data
     * @throws IOException
     */
    public void cacheQueue(byte[] data) throws IOException {
        /**
         * 缓存数据
         */
        FileChannel queueDataRandomAccessFileChannel = queueDataRandomAccessFile.getChannel();
        long preIndex = queueDataRandomAccessFileChannel.size();
        queueDataRandomAccessFileChannel.position(preIndex);
        queueDataRandomAccessFileChannel.write(ByteBuffer.wrap(data));
        long nowIndex = queueDataRandomAccessFileChannel.position();
        int length = new Long(nowIndex - preIndex).intValue();
        /**
         * 缓存索引
         */
        FileChannel queueIndexRandomAccessFileChannel = queueIndexRandomAccessFile.getChannel();
        QueueLength queueIndex = new QueueLength();
        queueIndex.setLength(length);
        byte[] serialize = SerializationUtil.serialize(length);
        byte[] bytes = new byte[INDEX_SIZE];
        System.arraycopy(serialize, 0, bytes, 0, serialize.length);
        long size = queueIndexRandomAccessFileChannel.size();
        queueIndexRandomAccessFileChannel.position(size);
        queueIndexRandomAccessFileChannel.write(ByteBuffer.wrap(bytes));
    }

    /**
     * 获得一批Queue
     *
     * @param size
     * @return
     * @throws Exception
     */
    public List<byte[]> fetchQueue(int size) throws Exception {
        //移除垃圾数据
        removeQueue();
        ArrayList<byte[]> returnBytes = new ArrayList<>();
        long totalDataIndex = 0;
        for (int j = 0; j < size; j++) {
            //获取索引开始
            QueueLength queueIndex = getIndex(j);
            //通过索引获取数据
            byte[] data = getData(totalDataIndex, queueIndex.getLength());
            returnBytes.add(data);
            totalDataIndex = totalDataIndex + queueIndex.getLength();
        }
        //设置上次读取的位置
        markIndex = size;
        return returnBytes;
    }


    /**
     * 获取记得数据值
     *
     * @param index 第index位置的数据的长度
     * @return
     * @throws Exception
     */
    private QueueLength getIndex(int index) throws Exception {
        FileChannel queueIndexRandomAccessFileChannel = queueIndexRandomAccessFile.getChannel();
        ByteBuffer buff = ByteBuffer.allocate(INDEX_SIZE);
        queueIndexRandomAccessFileChannel.read(buff, index * INDEX_SIZE);
        buff.flip();
        byte[] bytes = new byte[INDEX_SIZE];
        buff.get(bytes);
        int i = 0;
        for (; i < bytes.length; i++) {
            if (bytes[i] == 0) {
                break;
            }
        }
        byte[] b = new byte[i];
        System.arraycopy(bytes, 0, b, 0, i);
        return SerializationUtil.deserialize(b, QueueLength.class);
    }

    /**
     * 提取数据
     *
     * @param index  数据位置
     * @param length 数据长度
     * @return
     * @throws Exception
     */
    private byte[] getData(long index, int length) throws Exception {
        FileChannel queueDataRandomAccessFileChannel = queueDataRandomAccessFile.getChannel();
        ByteBuffer buffData = ByteBuffer.allocate(length);
        queueDataRandomAccessFileChannel.read(buffData, index);
        buffData.flip();
        byte[] byteData = new byte[length];
        buffData.get(byteData);
        return byteData;
    }


    /**
     * 移除消耗过的Queue
     *
     * @throws Exception
     */
    private void removeQueue() throws Exception {
        if (markIndex > 0) {
            //我们要移除之前的垃圾数据，
            for (int i = 0; i < markIndex; i++) {
                //通过索引读取长度
                QueueLength index = getIndex(i);
                int length = index.getLength();
                //移除数据 length

                //移除索引数据 10
            }
        }
    }

    public static void main(String[] args) throws Exception {

        QueueSerialization queueSerialization = new QueueSerialization();
        for (int i = 0; i < 100; i++) {
            User user = new User();
            user.setName("小王" + i);
            user.setAge(i);
            queueSerialization.cacheQueue(SerializationUtil.serialize(user));
        }

        List<byte[]> bytes = queueSerialization.fetchQueue(50);
        for (byte[] aByte : bytes) {
            System.out.println(SerializationUtil.deserialize(aByte, User.class));
        }


        List<byte[]> bytes2 = queueSerialization.fetchQueue(50);
        for (byte[] aByte : bytes2) {
            System.out.println(SerializationUtil.deserialize(aByte, User.class));
        }

        List<byte[]> bytes1 = queueSerialization.fetchQueue(50);
        for (byte[] aByte : bytes1) {
            System.out.println(SerializationUtil.deserialize(aByte, User.class));
        }


        List<byte[]> bytes3 = queueSerialization.fetchQueue(50);
        for (byte[] aByte : bytes3) {
            System.out.println(SerializationUtil.deserialize(aByte, User.class));
        }


    }


}
