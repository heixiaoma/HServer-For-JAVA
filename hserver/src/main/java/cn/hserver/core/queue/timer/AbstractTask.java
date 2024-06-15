package cn.hserver.core.queue.timer;

public abstract class AbstractTask {
	/**任务id. 如果是分布式部署多台应用，那次id要是全局唯一的*/
	private String id;
	private String queueName;
	/**第几圈*/
	private Integer cycleNum;

	/**
	 * 
	 * @param id 任务id
	 */
	public AbstractTask(String id,String queueName) {
		super();
		this.id = id;
		this.queueName = queueName;
	}


	public String getQueueName() {
		return queueName;
	}

	/**
	 * 	倒计数，为0时即可执行此任务
	 */
    public void countDown() {
        this.cycleNum--;
    }

	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public Integer getCycleNum() {
		return cycleNum;
	}


	public void setCycleNum(Integer cycleNum) {
		this.cycleNum = cycleNum;
	}

	@Override
	public String toString() {
		return "Task [id=" + id + ", cycleNum=" + cycleNum + "]";
	}

}