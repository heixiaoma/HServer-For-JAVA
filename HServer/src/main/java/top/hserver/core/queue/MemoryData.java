package top.hserver.core.queue;

import top.hserver.core.queue.fmap.FMap;

import java.io.File;
import java.util.Collection;

import static top.hserver.core.server.context.ConstConfig.PERSIST_PATH;

/**
 * 内存队列数据缓存，数据不大操作频繁，
 * 数据格式
 * key@data#
 */
public class MemoryData {

    private static String path = PERSIST_PATH + File.separator + "memory" + File.separator;

    private static final FMap<Object> stringFMap = new FMap<>(path + "memory.data", Object.class);

    static {
        File file = new File(PERSIST_PATH + File.separator + "memory");
        if (!file.isDirectory()){
            file.mkdirs();
        }
    }


    public static void add(String key,Object obj){
        stringFMap.put(key,obj);
    }

    public static void remove(String key){
        stringFMap.remove(key);
    }

    public static synchronized void sync(){
        stringFMap.syncFile();
    }

    public static synchronized Collection<Object> getAll(){
        return stringFMap.values();
    }

    public static synchronized void clear(){
        stringFMap.clear();
    }

    public static synchronized long size(){
        return stringFMap.size();
    }


    public static void main(String[] args) throws Exception {
//        for (int i = 0; i < 10000; i++) {
//            stringFMap.put(String.valueOf(i), String.valueOf(i));
//        }
//        stringFMap.syncFile();
        for (int i = 0; i < 10000; i++) {
            Object s = stringFMap.get(String.valueOf(i));
            if (s != null)
                System.out.println(s);
        }
        stringFMap.clear();
        stringFMap.syncFile();
    }

}
