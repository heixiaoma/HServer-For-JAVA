package cn.hserver.core.queue.timer;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Slot {
	/** 用来存放任务。也可更加时间情况使用其他容器存放，如：ConcurrentSkipListSet,Set, Map */
	private ConcurrentLinkedQueue<AbstractTask> tasks = new ConcurrentLinkedQueue<>();

	public ConcurrentLinkedQueue<AbstractTask> getTasks() {
		return tasks;
	}

	/**
	 * 加入一个任务
	 * 
	 * @param task
	 */
	protected void add(AbstractTask task) {
		tasks.add(task);
	}

	/**
	 * 删除一个任务
	 * 
	 * @param taskId
	 */
	protected void remove(String taskId) {
		Iterator<AbstractTask> taskIt = tasks.iterator();
		while (taskIt.hasNext()) {
			if (taskId.equals(taskIt.next().getId())) {
				taskIt.remove();
			}
		}
	}
}