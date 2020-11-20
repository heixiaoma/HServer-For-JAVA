package top.hserver.core.queue;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 块的信息
 *
 * @author hxm
 */
public class QueueBlockIndex {

    /**
     * 当前存在的块名列表和块大小
     */
    private TreeMap blockInfo = new TreeMap();

    /**
     * 当前块名
     */
    private String useBlockName;

    /**
     * 当前块使用的索引
     */
    private long position;


    public void addBlockName(String blockName) {
        this.blockInfo.put(blockName, new AtomicInteger(0));
    }

    public String getUseBlockName() {
        return useBlockName;
    }

    public void setUseBlockName(String useBlockName) {
        this.useBlockName = useBlockName;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }


    public boolean changeNext() {
        //移除第一文件
        blockInfo.remove(useBlockName);
        //删除文件
        new File(QueueSerialization.queuePath + useBlockName).delete();
        Map.Entry<String, AtomicInteger> newBlockInfo = blockInfo.lastEntry();
        if (newBlockInfo == null) {
            return false;
        }
        this.useBlockName = newBlockInfo.getKey();
        this.position = 0;
        return true;
    }

    /**
     * 索引加
     */
    public void incrementIndex() {
        Map.Entry<String, AtomicInteger> lastBlockInfo = blockInfo.lastEntry();
        lastBlockInfo.getValue().incrementAndGet();
    }


    /**
     * 获取最后一个块的大小
     *
     * @return
     */
    public int getLastQueueBlockSize() {
        Map.Entry<String, AtomicInteger> lastBlockInfo = blockInfo.lastEntry();
        if (lastBlockInfo == null) {
            return -1;
        }
        return lastBlockInfo.getValue().intValue();
    }

    /**
     * 获取最后一个块的名字
     *
     * @return
     */
    public String getLastQueueBlockName() {
        Map.Entry<String, AtomicInteger> lastBlockInfo = blockInfo.lastEntry();
        return lastBlockInfo.getKey();
    }


}
