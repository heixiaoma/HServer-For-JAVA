package test5;

import test1.ioc.B;
import top.hserver.cloud.util.SerializationUtil;
import top.hserver.core.server.context.ConstConfig;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class TestIndex {
    private static final String queueIndex = ConstConfig.PATH + "queue" + File.separator + "QueueIndex.queue";



    public static void main(String[] args) throws Exception{
        for (int j = 1; j < 1000000000; j++) {
//            301770
//            byte[] bytes = fetchQueue(j);
//            List<Byte> a = new ArrayList<>();
//            for (int i = 0; i < bytes.length; i++) {
//                if (bytes[i] != 0) {
//                    a.add(bytes[i]);
//                } else {
//                    break;
//                }
//            }
//            byte[] bytes1 = new byte[a.size()];
//            for (int i = 0; i < a.size(); i++) {
//                bytes1[i] = a.get(i);
//            }
//            Index deserialize = SerializationUtil.deserialize(bytes1, Index.class);
//            System.out.println(deserialize.getIndex());
        write(j);
        }
    }

    public static void write(int j) throws Exception{
        RandomAccessFile file=new RandomAccessFile(queueIndex,"rw");
        FileChannel channel = file.getChannel();
        Index index = new Index();
        index.setIndex(j);
        byte[] serialize = SerializationUtil.serialize(index);
        byte[] bytes = new byte[10];
        System.arraycopy(serialize,0,bytes,0,serialize.length);
        long size = channel.size();
        channel.position(size);
        channel.write(ByteBuffer.wrap(bytes));
        long position = channel.position();
        System.out.println(position-size+"---"+j);
    }


    public static byte[] fetchQueue(int j) throws Exception{
        RandomAccessFile file=new RandomAccessFile(queueIndex,"rw");
        FileChannel channel = file.getChannel();
        ByteBuffer buff = ByteBuffer.allocate(10);
        channel.read(buff,j*10);
        buff.flip();
        byte[] bytes=new byte[10];
        buff.get(bytes);
        return bytes;
    }

}
