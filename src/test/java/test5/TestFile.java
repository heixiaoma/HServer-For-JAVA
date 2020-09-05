package test5;

import top.hserver.core.server.context.ConstConfig;

import java.io.File;
import java.io.FileDescriptor;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class TestFile {
    private final static String testPath = ConstConfig.PATH + "queue" + File.separator + "test.txt";

    public static void main(String[] args) throws Exception {

        RandomAccessFile randomAccessFile = new RandomAccessFile(testPath, "rw");
        FileChannel channel = randomAccessFile.getChannel();
        channel.position(0);
        channel.write(ByteBuffer.wrap("111".getBytes()));
        long size = channel.size();
        channel.position(size);
        channel.write(ByteBuffer.wrap("222".getBytes()),0);
        //删除 5 position后面的数据
//        channel.truncate(5);
    }
}
