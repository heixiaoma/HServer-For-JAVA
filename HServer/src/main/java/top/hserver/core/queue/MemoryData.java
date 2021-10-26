package top.hserver.core.queue;

import top.hserver.cloud.util.SerializationUtil;
import top.hserver.core.queue.fmap.FMap;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static top.hserver.core.server.context.ConstConfig.PERSIST_PATH;

/**
 * 内存队列数据缓存，数据不大操作频繁，
 * 数据格式
 * key@data#
 */
public class MemoryData {
    private static String path = PERSIST_PATH + File.separator + "memory" + File.separator;

    public static void main(String[] args) throws Exception {
        FMap<String> stringFMap = new FMap<>(path + "memory.data", String.class);
        for (int i = 0; i < 10000; i++) {
            stringFMap.put(String.valueOf(i), String.valueOf(i));
        }
        stringFMap.syncFile();
        for (int i = 0; i < 10000; i++) {
            String s = stringFMap.get(String.valueOf(i));
            if (s != null)
                System.out.println(s);
        }
        stringFMap.clear();
        stringFMap.syncFile();
    }

}
