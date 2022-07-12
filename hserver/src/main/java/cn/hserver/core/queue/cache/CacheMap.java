package cn.hserver.core.queue.cache;


import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;

import java.io.File;
import java.util.Map;
import java.util.Set;

public abstract class CacheMap<K, V> {
    private DB db;

    public CacheMap(String path) {
        try {
            Iq80DBFactory factory = Iq80DBFactory.factory;
            Options options = new Options();
            db = factory.open(new File("fun"), options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int size() {
        DBIterator iterator1 = db.iterator();
        int i = 0;
        while (iterator1.hasNext()) {
            iterator1.next();
            i++;
        }
        return i;
    }

    abstract boolean containsKey(Object key);

    abstract boolean containsValue(Object value);

    abstract V get(Object key);

    abstract V put(K key, V value);

    abstract V remove(Object key);

    abstract void clear();

    abstract Set<Map.Entry<K, V>> entrySet();
}
