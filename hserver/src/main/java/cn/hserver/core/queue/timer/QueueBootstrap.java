package cn.hserver.core.queue.timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class QueueBootstrap {
	private final ScheduledExecutorService newScheduledThreadPool = Executors.newScheduledThreadPool(1);

	public WheelQueue start() {
		WheelQueue wheelQueue = new WheelQueue();
		// 定义任务
		QueueScanTimer timerTask = new QueueScanTimer(wheelQueue);
		// 设置任务的执行，1秒后开始，每1秒执行一次
		newScheduledThreadPool.scheduleWithFixedDelay(timerTask, 1, 1, TimeUnit.SECONDS);
		return wheelQueue;
	}
	/**
	 * 停止此队列运行。
	 */
	public void shutdown() {
		// 只停止扫描队列。已运行的任务暂不停止。
        newScheduledThreadPool.shutdown();
    }

}
