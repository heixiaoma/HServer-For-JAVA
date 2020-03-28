package top.hserver.core.event.queue;


import top.hserver.core.event.queue.util.DataByteArrayOutputStream;
import top.hserver.core.event.queue.util.Utilities;

import java.util.ArrayList;

public abstract class BasePersistence implements PersistenceIntf
{
	private long maxByteArray_Sz = 50 * 1024 * 1024;
	private ArrayList<byte[]> inMemoryDataList = new ArrayList();
	private int curInMemorySz = 0;
	private final Object ListMutex = new Object();
	private final Object WriteAndReadMutex = new Object();
	private Thread thread;
	private int cnt;
	private boolean isHaveDataInPersistence = false;
	private DataByteArrayOutputStream theOutBytes = null;
	private int oneBatchWriteCnt = 20;
	private long isCanReleaseResTime = -1;
	private long isCanReleaseResMaxTime = 60 * 1000;
	private int writeOffset = 0;
	
	/**
	 * 
	 * @param maxByteArray_SzParm         最大允许的内存数,单位byte
	 * @param oneBatchWriteCntParm        一次序列化批量处理的byte[]的个数
	 * @param isCanReleaseResMaxTimeParm  如果连续等待这么长时间还没有任何持久化的读、写操作，
	 *                                    则删除相关资源，如保存序列化的文件。
	 * @throws Exception
	 */
	public BasePersistence(long maxByteArray_SzParm,
			int oneBatchWriteCntParm, int isCanReleaseResMaxTimeParm) throws Exception
	{
		maxByteArray_Sz = maxByteArray_SzParm;
		oneBatchWriteCnt = oneBatchWriteCntParm;
		isCanReleaseResMaxTime = isCanReleaseResMaxTimeParm;
		theOutBytes = new DataByteArrayOutputStream(1 * 1024 * 1024);
		
		thread = new Thread() {
            public void run() {
                processQueue();
            }
        };
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.setDaemon(true);
        thread.setName("sponge Data File Writer");
        thread.start();
	}
	
	@Override
	public boolean addOneBatchBytes(byte[] bytesParm)
	{
		boolean retBool = false;
		cnt++;
		
		if (isCanReleaseResTime != -1)
        {
        	if ((System.currentTimeMillis() - isCanReleaseResTime)
        					> isCanReleaseResMaxTime)
        	{
        		try
        		{
        			canReleaseRes();
        		}
        		catch (Exception ex)
        		{
        			ex.printStackTrace();
        		}
        		isCanReleaseResTime = -1;
        	}
        }
		
		if (curInMemorySz + bytesParm.length <= maxByteArray_Sz)
		{
			retBool = true;
			synchronized (ListMutex)
			{
				inMemoryDataList.add(bytesParm);
				ListMutex.notifyAll();
			}
			
			curInMemorySz += bytesParm.length;
		}
		else
		{
			System.out.println("已经达到缓冲器系统处理上线,丢弃此次数据,数据大小 "+bytesParm.length
					+"。原因是磁盘IO资源不足，请确认!!!");
		}
		
		return retBool;
	}

	protected void processQueue() {
		byte[] tmpBytes = null;
        try {
            while (true) {
                // Block till we get a command.
                synchronized (ListMutex) {
                    while (true) {
                    	int tmpListSz = inMemoryDataList.size();
                        if (tmpListSz > 0)
                        {
                        	int tmpThisTimeSaveCnt = oneBatchWriteCnt;
                        	if (tmpListSz < oneBatchWriteCnt)
                        	{
                        		tmpThisTimeSaveCnt = tmpListSz;
                        	}
                        	for (int i = 0; i < tmpThisTimeSaveCnt; i++)
                        	{
                        		tmpBytes = inMemoryDataList.remove(0);
                        		curInMemorySz -= tmpBytes.length;
                        		theOutBytes.write(tmpBytes);
                        	}
                            break;
                        }
                        ListMutex.wait();
                    }
                    ListMutex.notifyAll();
                }
                
                if (theOutBytes.size() > 0)
                {
                	synchronized(WriteAndReadMutex)
                	{
                		doWriteOneBatchBytes(theOutBytes.getData(), writeOffset, theOutBytes.size());
                		theOutBytes.reset();
                	}
                }
            }
        }
        catch (Exception e)
        {
            // TODO
        	e.printStackTrace();
        }
        finally {
            try {
            	destroy();
            } catch (Throwable ignore) {
            }
        }
    }
	
	@Override
	public byte[] fetchOneBatchBytes() throws SpongeException
	{
		byte[] retBytes = null;
		try
		{
			synchronized(WriteAndReadMutex)
			{
				retBytes = doFetchOneBatchBytes();
				
				if (retBytes == null)
				{
					isCanReleaseResTime = System.currentTimeMillis();
				}
				else
				{
					isCanReleaseResTime = -1;
				}
				
				if (retBytes == null)
				{
					if (theOutBytes.size() > 0)
					{
						int tmpByteLength = Utilities.getIntFromBytes(theOutBytes.getData(), writeOffset + 2);
						byte[] tmpReadBytes = new byte[tmpByteLength];
						
						System.arraycopy(theOutBytes.getData(), writeOffset, tmpReadBytes, 0, tmpByteLength);
						retBytes = tmpReadBytes;
						
						writeOffset += tmpByteLength;
					}
				}
				
				if (retBytes == null)
				{
					synchronized (ListMutex) {
						if (inMemoryDataList.size() > 0)
						{
							retBytes = inMemoryDataList.remove(0);
							curInMemorySz -= retBytes.length;
						}
					}
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			throw new SpongeException(ex.getMessage());
		}
		
		return retBytes;
	}

	public abstract byte[] doFetchOneBatchBytes() throws Exception;
	public abstract void doWriteOneBatchBytes(byte[] writeBytesParm, int offsetParm, int lengthParm) throws Exception;
	public abstract void doWriteOneBatchBytes(byte[] writeBytesParm) throws Exception;
	public abstract void destroy() throws Exception;
	public abstract void canReleaseRes() throws Exception;

	@Override
	public boolean isHaveDataInPersistence()
	{
		return isHaveDataInPersistence;
	}

	public void setHaveDataInPersistence(boolean isHaveDataInPersistence)
	{
		this.isHaveDataInPersistence = isHaveDataInPersistence;
	}
}
