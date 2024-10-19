package cn.hserver.core.queue.fqueue;


import cn.hserver.core.queue.QueueData;
import cn.hserver.core.queue.QueueDispatcher;
import cn.hserver.core.queue.QueueHandleInfo;
import cn.hserver.core.queue.fqueue.exception.FileFormatException;
import cn.hserver.core.server.util.NamedThreadFactory;
import cn.hserver.core.server.util.SerializationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

    private ExecutorService executorService;

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
            synchronized (this) {
                this.notifyAll();
            }
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
            fsQueue = null;
        }
        stopHandler();
    }

    public void stopHandler() {
        if (executorService != null&&!executorService.isShutdown()) {
            List<Runnable> runnables = executorService.shutdownNow();
            runnables.forEach(Runnable::run);
            executorService = null;
        }
    }

    public void restartHandler() {
        if (executorService == null || executorService.isShutdown()) {
            start();
        }
    }

    public void start() {
        QueueHandleInfo queueHandleInfo = QueueDispatcher.getQueueHandleInfo(queueName);
        if (queueHandleInfo == null) {
            return;
        }
        int threadSize = queueHandleInfo.getThreadSize();
        executorService = Executors.newFixedThreadPool(threadSize, new NamedThreadFactory(queueName + "-queue-handler"));
        for (int i = 0; i < threadSize; i++) {
            executorService.submit(() -> {
                while (fsQueue != null && executorService != null && !executorService.isShutdown()) {
                    try {
                        if (threadSize == 1) {
                            byte[] peek = peek();
                            if (peek != null) {
                                QueueData deserialize = SerializationUtil.deserialize(peek, QueueData.class);
                                queueHandleInfo.getQueueEventHandler().invoke(deserialize);
                                poll();
                            } else {
                                synchronized (FQueue.this) {
                                    FQueue.this.wait();
                                }
                            }
                        } else {
                            byte[] poll = poll();
                            if (poll != null) {
                                QueueData deserialize = SerializationUtil.deserialize(poll, QueueData.class);
                                queueHandleInfo.getQueueEventHandler().invoke(deserialize);
                            } else {
                                synchronized (FQueue.this) {
                                    FQueue.this.wait();
                                }
                            }
                        }
                    } catch (Exception e) {
                        if (e instanceof InterruptedException) {
                            return;
                        }
                        log.error(e.getMessage(), e);
                        return;
                    }
                }
            });
        }
    }
}
