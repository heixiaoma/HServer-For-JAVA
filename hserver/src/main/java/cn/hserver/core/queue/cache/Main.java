package cn.hserver.core.queue.cache;


import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;

import java.io.File;

import static org.iq80.leveldb.impl.Iq80DBFactory.bytes;

public class Main {
    public static void main(String[] args) throws Exception {
        Iq80DBFactory factory = Iq80DBFactory.factory;
        Options options = new Options();
        DB db = factory.open(new File("fun"), options);
        for (int i = 0; i < 10000000; i++) {
            db.put(bytes(String.valueOf(i)),bytes(String.valueOf(i)));
        }
        System.out.println("start");
        DBIterator iterator1 = db.iterator();
        long l = System.currentTimeMillis();
        int i=0;
        while (iterator1.hasNext()){
            iterator1.next();
            i++;
        }
        System.out.println(i);
        System.out.println((System.currentTimeMillis()-l)/1000.0);
        System.exit(0);
    }
}
