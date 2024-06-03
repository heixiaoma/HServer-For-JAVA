package cn.hserver.core.queue.fqueue;


import cn.hserver.HServerApplication;
import cn.hserver.core.queue.QueueData;
import cn.hserver.core.queue.QueueDispatcher;
import cn.hserver.core.queue.QueueHandleInfo;
import cn.hserver.core.queue.QueueInfo;
import cn.hserver.core.queue.fqueue.exception.FileFormatException;
import cn.hserver.core.server.util.ExceptionUtil;
import cn.hserver.core.server.util.NamedThreadFactory;
import cn.hserver.core.server.util.SerializationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 基于文件系统的持久化队列
 */

public class FQueue extends AbstractQueue<byte[]> implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(FQueue.class);


    private static final long serialVersionUID = -1L;

    private FSQueue fsQueue = null;
    private String queueName = null;
    private Lock lock = new ReentrantReadWriteLock().writeLock();

    public FQueue(String path, String queueName) throws IOException, FileFormatException {
        this.queueName = queueName;
        fsQueue = new FSQueue(path);
    }

    /**
     * 创建一个持久化队列
     *
     * @param path              文件的存储路径
     * @param entityLimitLength 存储数据的单个文件的大小
     * @throws IOException
     * @throws FileFormatException
     */
    public FQueue(String path, int entityLimitLength) throws IOException, FileFormatException {
        fsQueue = new FSQueue(path, entityLimitLength);
    }

    public FQueue(File dir) throws IOException, FileFormatException {
        fsQueue = new FSQueue(dir);
    }

    /**
     * 创建一个持久化队列
     *
     * @param entityLimitLength 存储数据的单个文件的大小
     * @throws IOException
     * @throws FileFormatException
     */
    public FQueue(File dir, int entityLimitLength) throws IOException, FileFormatException {
        fsQueue = new FSQueue(dir, entityLimitLength);
    }

    @Override
    public Iterator<byte[]> iterator() {
        throw new UnsupportedOperationException("iterator Unsupported now");
    }

    @Override
    public int size() {
        return fsQueue.getQueueSize();
    }

    @Override
    public boolean offer(byte[] e) {
        try {
            lock.lock();
            fsQueue.add(e);
            return true;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (FileFormatException ex) {
        } finally {
            lock.unlock();
        }
        return false;
    }

    @Override
    public byte[] peek() {
        try {
            lock.lock();
            return fsQueue.readNext();
        } catch (IOException ex) {
            return null;
        } catch (FileFormatException ex) {
            return null;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public byte[] poll() {
        try {
            lock.lock();
            return fsQueue.readNextAndRemove();
        } catch (IOException ex) {
            return null;
        } catch (FileFormatException ex) {
            return null;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void clear() {
        try {
            lock.lock();
            fsQueue.clear();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (FileFormatException e) {
        } finally {
            lock.unlock();
        }
    }

    /**
     * 关闭文件队列
     *
     * @throws IOException
     * @throws FileFormatException
     */
    public void close() throws IOException, FileFormatException {
        if (fsQueue != null) {
            fsQueue.close();
        }
        fsQueue = null;
    }

    private void sleep() {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            log.error(ExceptionUtil.getMessage(e));
        }
    }

    public void start() {
        new NamedThreadFactory("hserver_queue:"+queueName).newThread(() -> {
                    while (fsQueue != null) {
                        try {
                            QueueHandleInfo queueHandleInfo = QueueDispatcher.getQueueHandleInfo(queueName);
                            if (queueHandleInfo == null) {
                                sleep();
                                return;
                            }
                            QueueInfo queueInfo = queueHandleInfo.getQueueFactory().queueInfo();
                            int threadSize = queueHandleInfo.getThreadSize();
                            if (queueInfo != null ) {
                                byte[] poll = null;
                                if (threadSize == 1) {
                                    if ((queueInfo.getBufferSize() - queueInfo.getRemainQueueSize() < threadSize)) {
                                        poll = peek();
                                    }
                                } else {
                                    if (queueInfo.getRemainQueueSize()>0) {
                                        poll = poll();
                                    }
                                }
                                if (poll != null) {
                                    QueueData deserialize = SerializationUtil.deserialize(poll, QueueData.class);
                                    QueueDispatcher.dispatcherQueue(deserialize, deserialize.getQueueName());
                                } else {
                                    sleep();
                                }
                            } else {
                                sleep();
                            }
                        } catch (Exception e) {
                            sleep();
                            log.error(ExceptionUtil.getMessage(e));
                        }
                    }
                }
        ).start();
    }
}
