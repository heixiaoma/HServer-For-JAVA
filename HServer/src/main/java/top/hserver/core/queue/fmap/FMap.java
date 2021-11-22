package top.hserver.core.queue.fmap;

import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;
import top.hserver.cloud.util.SerializationUtil;
import top.hserver.core.queue.fqueue.FQueue;
import top.hserver.core.queue.fqueue.exception.FileFormatException;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 不准给别人用，他们坏得很，怕数据错了
 *
 * @param <V>
 */
final class FMap<V> extends ConcurrentHashMap<String, V> {

    private Class<V> clazz;
    private String path;
    private FQueue fQueue=null;

    public  FMap(String path, Class<V> clazz) {
        this.clazz = clazz;
        this.path = path;
        try {
            if (fQueue==null) {
                fQueue = new FQueue(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FileFormatException e) {
            e.printStackTrace();
        }

        List<Memory> memories = readAll();
        for (Memory memory : memories) {
            put(memory.key, SerializationUtil.deserialize(memory.getValues(), clazz));
        }
    }


    public synchronized void syncFile() {
        fQueue.clear();
       this.forEach((k,v)->{
           fQueue.offer(SerializationUtil.serialize(new Memory(k,SerializationUtil.serialize(v))));
       });
    }


    private synchronized List<Memory> readAll() {
        List<Memory> data=new ArrayList<>();
        while (true) {
            byte[] peek = fQueue.poll();
            if (peek==null){
                return data;
            }
            Memory deserialize = SerializationUtil.deserialize(peek, Memory.class);
            data.add(deserialize);
        }
    }

    public static class Memory implements Serializable {
        private String key;
        private byte[] values;

        public Memory(String key, byte[] values) {
            this.key = key;
            this.values = values;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public byte[] getValues() {
            return values;
        }

        public void setValues(byte[] values) {
            this.values = values;
        }

        @Override
        public String toString() {
            return "Memory{" +
                    "key='" + key + '\'' +
                    ", values=" + Arrays.toString(values) +
                    '}';
        }
    }


}
