package top.hserver.core.queue;


import top.hserver.core.queue.kvstore.core.KVStore;
import top.hserver.core.queue.kvstore.core.RocksDBConfiguration;

import java.io.File;
import java.util.Collection;
import java.util.Optional;

import static top.hserver.core.server.context.ConstConfig.PERSIST_PATH;

public class FQueue {

    private final KVStore<Long, QueueData> tree;
    private final KVStore<Long, QueueData> back_tree;

    public FQueue(String queueName) {
        String path = PERSIST_PATH + File.separator + queueName + File.separator;
        tree = new KVStore<>(new RocksDBConfiguration(path, "map"),Long.class,QueueData.class);
        back_tree = new KVStore<>(new RocksDBConfiguration(path, "back_map"),Long.class,QueueData.class);
        //恢复back
        reBack();
    }

    public void reBack() {
        try {
            Collection<QueueData> queueData = back_tree.findAll();
            for (QueueData queueDatum : queueData) {
                System.out.println("重复:"+queueDatum.getId());
                tree.save(queueDatum.getId(), queueDatum);
            }
            back_tree.deleteAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clear() {
        try{
            tree.deleteAll();
        }catch (Exception e){

        }
    }

    public long size() {
        try {
            return tree.size();
        } catch (Exception e) {
            return 0;
        }
    }

    public void add(QueueData queueData) {
        try {
            tree.save(queueData.getId(), queueData);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public QueueData getNext() {
        try {
            Optional<QueueData> first = tree.findLast();
            QueueData queueData = first.orElse(null);
            if (queueData==null){
                return null;
            }
            back_tree.save(queueData.getId(), queueData);
            tree.deleteByKey(queueData.getId());
            return queueData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void removeBack(Long id) {
        try {
            back_tree.deleteByKey(id);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
