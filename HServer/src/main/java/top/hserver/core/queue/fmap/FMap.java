package top.hserver.core.queue.fmap;

import top.hserver.cloud.util.SerializationUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
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

public class FMap<V> extends HashMap<String, V> {

    private Class<V> clazz;
    private String path;

    public FMap(String path, Class<V> clazz) {
        this.clazz = clazz;
        this.path = path;
        List<Memory> memories = readAll();
        for (Memory memory : memories) {
            put(memory.key, SerializationUtil.deserialize(memory.getValues(), clazz));
            System.out.println(memory.getKey());
        }
    }


    public void syncFile() {
        RandomAccessFile randomAccessFileData = null;
        FileChannel fileChannel = null;
        try {
            randomAccessFileData = new RandomAccessFile(path, "rw");
            fileChannel = randomAccessFileData.getChannel();
            fileChannel.truncate(0);
            MappedByteBuffer data = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, 1024 * 1024);
            final int[] index = {0};
            this.forEach((k, v) -> {
                try {
                    data.position(index[0]);
                    data.put(k.getBytes(StandardCharsets.UTF_8));
                    data.put("@".getBytes(StandardCharsets.UTF_8));
                    data.put(SerializationUtil.serialize(v));
                    data.put("#".getBytes(StandardCharsets.UTF_8));
                    index[0] = data.position();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            if (data != null) {
                try {
                    clean(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileChannel != null) {
                try {
                    fileChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (randomAccessFileData != null) {
                try {
                    randomAccessFileData.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }


    private List<Memory> readAll() {
        List<Memory> memoryList = new ArrayList<>();
        RandomAccessFile randomAccessFileData = null;
        FileChannel fileChannel = null;
        MappedByteBuffer data = null;
        try {
            randomAccessFileData = new RandomAccessFile(path, "rw");
            fileChannel = randomAccessFileData.getChannel();
            data = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, 1024 * 1024);
            int start = 0;
            List<Byte> dataMap = new ArrayList<>();
            while (true) {
                byte b = data.get(start);
                dataMap.add(b);
                if (b == '#' && start != 0) {
                    int keyFlag = -1;
                    for (int i = 0; i < dataMap.size(); i++) {
                        if (dataMap.get(i) == '@') {
                            keyFlag = i;
                            break;
                        }
                    }
                    if (keyFlag == -1) {
                        continue;
                    }
                    byte[] bytesKey = new byte[keyFlag];
                    byte[] bytesData = new byte[dataMap.size() - keyFlag - 2];
                    int index = 0;
                    for (int i = 0; i < dataMap.size(); i++) {
                        if (i < keyFlag) {
                            bytesKey[i] = dataMap.get(i);
                        } else if (i > keyFlag && i - keyFlag <= (dataMap.size() - keyFlag - 2)) {
                            bytesData[index] = dataMap.get(i);
                            index++;
                        }
                    }
                    memoryList.add(new Memory(new String(bytesKey), bytesData));
                    dataMap.clear();
                }
                start++;

            }
        } catch (Exception e) {

        } finally {
            if (fileChannel != null) {
                try {
                    fileChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (randomAccessFileData != null) {
                try {
                    randomAccessFileData.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (data != null) {
                try {
                    clean(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return memoryList;
    }

    public void clean(final Object buffer) throws Exception {
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
