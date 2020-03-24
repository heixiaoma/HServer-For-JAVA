package top.hserver.core.eventx;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * 事件处理队列
 */
public class EventHandleQueue extends SortedBlockingQueue<Runnable> {
    private static final long serialVersionUID = 2323031878044079162L;

    @Override
    public Runnable take() throws InterruptedException {
        Runnable task = super.take();
        increRemainsPriority();
        return task;
    }

    @Override
    public Runnable poll(long timeout, TimeUnit unit) throws InterruptedException {
        Runnable task = super.poll(timeout, unit);
        if (task != null) {
            increRemainsPriority();
        }
        return task;
    }

    /**
     * 提升剩余任务的优先级
     */
    private void increRemainsPriority() {
        Iterator<Runnable> it = this.iterator();
        while (it.hasNext()) {
            ((EventHandleTask) it.next()).increPriority();
        }
    }
}
