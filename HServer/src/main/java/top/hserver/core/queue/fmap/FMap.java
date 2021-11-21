package top.hserver.core.queue.fmap;

import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;
import top.hserver.cloud.util.SerializationUtil;

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

public class FMap<V> extends HashMap<String, V> {

    private Class<V> clazz;
    private String path;
    private int size=1024*1024;

    public FMap(String path, Class<V> clazz){
        this(path,clazz,-1);
    }
    public  FMap(String path, Class<V> clazz,int size) {
        this.clazz = clazz;
        this.path = path;
        if (size>0) {
            this.size = size;
        }
        List<Memory> memories = readAll();
        for (Memory memory : memories) {
            put(memory.key, SerializationUtil.deserialize(memory.getValues(), clazz));
        }
    }


    public void syncFile() {

    }


    private List<Memory> readAll() {

    }

    private void clean(final Object buffer) throws Exception {
        AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                try {
                    Method getCleanerMethod = buffer.getClass().getMethod("cleaner", new Class[0]);
                    getCleanerMethod.setAccessible(true);
                    sun.misc.Cleaner cleaner = (sun.misc.Cleaner) getCleanerMethod.invoke(buffer, new Object[0]);
                    cleaner.clean();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
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
