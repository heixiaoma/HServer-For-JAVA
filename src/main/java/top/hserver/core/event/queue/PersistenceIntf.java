package top.hserver.core.event.queue;

public interface PersistenceIntf
{
	/**
	 * 保存一次数据到持久化介质
	 * @param bytesParm
	 * @return
	 */
	public boolean addOneBatchBytes(byte[] bytesParm);
	
	/**
	 * 从持久化介质获取一批任务
	 * @return
	 * @throws SpongeException
	 */
	public byte[] fetchOneBatchBytes() throws SpongeException;
	
	/**
	 * 启动的时候，该存储介质是否有没被消费的任务,如果有返回true,如果没有返回false;
	 * @return
	 */
	public boolean isHaveDataInPersistence();
}
