package cn.hserver.core.queue.cache;


import cn.hserver.core.server.util.SerializationUtil;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static org.iq80.leveldb.impl.Iq80DBFactory.bytes;

public class CacheMap<V> {
    private DB db;
    private Class<V> aClass;

    public CacheMap(String path, Class<V> aClass) {
        this(path, aClass, new Options());
    }

    public CacheMap(String path, Class<V> aClass, Options options) {
        try {
            this.aClass = aClass;
            Iq80DBFactory factory = Iq80DBFactory.factory;
            db = factory.open(new File(path), options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int size() {
        int i = 0;
        for (Map.Entry<byte[], byte[]> ignored : db) {
            i++;
        }
        return i;
    }

    public boolean containsKey(String key) {
        for (Map.Entry<byte[], byte[]> next : db) {
            if (key.equals(new String(next.getKey()))) {
                return true;
            }
        }
        return false;
    }


    /**
     * 获取一个
     *
     * @return
     */
    public V getFirst() {
        for (Map.Entry<byte[], byte[]> next : db) {
            return SerializationUtil.deserialize(next.getValue(), aClass);
        }
        return null;
    }

    public V get(String key) {
        byte[] bytes = db.get(bytes(key));
        if (bytes != null) {
            return SerializationUtil.deserialize(bytes, aClass);
        }
        return null;
    }

    public void put(String key, V value) {
        db.put(bytes(key), SerializationUtil.serialize(value));
    }

    public void remove(String key) {
        db.delete(bytes(key));
    }

    public void clear() {
        for (Map.Entry<byte[], byte[]> next : db) {
            db.delete(next.getKey());
        }
    }

    public void close() {
        try {
            db.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public DB getDb() {
        return db;
    }
}
