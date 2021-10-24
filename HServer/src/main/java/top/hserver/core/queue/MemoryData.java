package top.hserver.core.queue;

import top.hserver.cloud.util.SerializationUtil;

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
    private String path = PERSIST_PATH + File.separator + "memory" + File.separator;
    private RandomAccessFile randomAccessFileData;
    private RandomAccessFile randomAccessFileIndex;
    private MappedByteBuffer data;
    private MappedByteBuffer index;

    public MemoryData() {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        try {
            randomAccessFileData = new RandomAccessFile(path + "memory.data", "rw");
            randomAccessFileIndex = new RandomAccessFile(path + "memory.index", "rw");
            data = randomAccessFileData.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, 1024 * 1024);
            index = randomAccessFileIndex.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, 10);
        } catch (Exception e) {
        }
    }

    public void put(String key, Object value) {
        try {
            data.position(index.getInt(0));
            data.put(key.getBytes(StandardCharsets.UTF_8));
            data.put("@".getBytes(StandardCharsets.UTF_8));
            data.put(SerializationUtil.serialize(value));
            data.put("#".getBytes(StandardCharsets.UTF_8));
            index.position(0);
            index.putInt(data.position());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void remove(String key) {
        int start = 0;
        List<Byte> dataMap = new ArrayList<>();
        try {
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
                    if (key.equals(new String(bytesKey))) {
                        if (dataMap.size() > start) {
                            index = 0;
                        } else {
                            index = (start - (dataMap.size() + keyFlag));
                        }

                        //没想好怎么写，休息下，
                        //todo
                        System.out.println("开始位置：" + index);
                        System.out.println("结束位置位置：" + start);


                    }
                    dataMap.clear();
                }
                start++;

            }
        } catch (Exception e) {

        }


    }

    public List<Memory> readAll() {
        List<Memory> memoryList = new ArrayList<>();
        int start = 0;
        List<Byte> dataMap = new ArrayList<>();
        try {
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

        }
        return memoryList;
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

    public static void main(String[] args) throws Exception {
        MemoryData memoryData = new MemoryData();
        System.out.println(SerializationUtil.serialize(new Memory("aaa", null)));
        for (int i = 0; i < 10; i++) {
            memoryData.put("899" + i, new Memory("aaa" + i, null));
        }

        memoryData.remove("8991");

        List<Memory> memories = memoryData.readAll();
        for (Memory memory : memories) {
            System.out.println(memory.getKey());
        }
    }

}
