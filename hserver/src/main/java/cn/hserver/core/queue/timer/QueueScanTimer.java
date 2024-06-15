package cn.hserver.core.queue.timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class QueueScanTimer extends TimerTask {
	private static final Logger LOG = LoggerFactory.getLogger(QueueScanTimer.class);
	/**环形队列*/
	private final WheelQueue queue;
	
	/**处理每个槽位的线程，循环到这个槽位，立即丢到一个线程去处理，然后继续循环队列。*/
	private final ThreadPoolExecutor slotPool = new ThreadPoolExecutor(2, 10,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>());

	public QueueScanTimer(WheelQueue queue) {
		super();
		this.queue = queue;
	}

	@Override
	public void run() {
		try {
			if (queue == null) {
				return;
			}
			Calendar calendar = Calendar.getInstance();
			int currentSecond = calendar.get(Calendar.MINUTE) * 60 + calendar.get(Calendar.SECOND);
			Slot slot = queue.peek(currentSecond);
			slotPool.execute(new SlotTask(slot.getTasks(), currentSecond));
		} catch (Exception e) {
			//这里一个槽位的屏蔽异常，继续执行。
			LOG.error(e.getMessage(), e);;
		}
	}

	/**
	 * 槽位任务
	 * @author hongjian.liu
	 *
	 */
    final class SlotTask implements Runnable {
    	ConcurrentLinkedQueue<AbstractTask> tasks;
    	int currentSecond;
    	
    	
		public SlotTask(ConcurrentLinkedQueue<AbstractTask> tasks, int currentSecond) {
			super();
			this.tasks = tasks;
			this.currentSecond = currentSecond;
		}

		@Override
		public void run() {
			if (tasks == null) {
				return;
			}
			String taskId;
			Iterator<AbstractTask> it = tasks.iterator();
            while (it.hasNext()) {
            	AbstractTask task = it.next();
                taskId = task.getId();
                if (task.getCycleNum() <= 0) {


					//todo 时间到了需要执行 task
                    it.remove();
                    queue.getTaskSlotMapping().remove(taskId);
                } else {
                	if (LOG.isDebugEnabled()) {
                		LOG.debug("countDown#running_current_solt:currentSecond={}, task={}", currentSecond, task.toString());
                	}
                    task.countDown();
                }
            }
		}
	}
    

}