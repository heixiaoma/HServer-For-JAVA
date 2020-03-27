package top.hserver.core.event.queue.util;



/**
 * @author netcomm
 * @version $Revision: 1.2 $ $Date: 2007/05/14 05:35:18 $
 */
public class Utilities extends Object
{
	private static Utilities service = new Utilities();
    
	private Utilities()
	{
        
	}

	public static Utilities getInstance()
	{
		return service;
	}
	
	public static void printUsedMemory()
    {
        long tmpLong = (Runtime.getRuntime().totalMemory() - Runtime
            .getRuntime()
            .freeMemory())
            / (1024 * 1024);
        System.out.println("当前消耗内存 " + Long.toString(tmpLong) + " M");
    }
	
    /*
     * Wait for a shutdown invocation elsewhere
     * 
     * @throws Exception
     */
    public static void waitForShutdown() throws Exception {
        final boolean[] shutdown = new boolean[] {
            false
        };
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                synchronized (shutdown)
                {
                    shutdown[0] = true;
                    shutdown.notify();
                }
            }
        });

        // Wait for any shutdown event
        synchronized (shutdown) {
            while (!shutdown[0]) {
                try {
                    shutdown.wait();
                } catch (InterruptedException e) {
                }
            }
        }
    }
    
    public static byte[] bytesAppendByteArray(byte[] appendBytes, byte[] data)
	{
		byte[] newB = new byte[appendBytes.length + data.length];
		System.arraycopy(appendBytes, 0, newB, 0, appendBytes.length);
		System.arraycopy(data, 0, newB, appendBytes.length, data.length);
		data = null;
		return newB;
	}
    
    public static byte[] bytesAppendByteArray(byte[] appendBytes, byte data)
	{
		byte[] newB = new byte[appendBytes.length + 1];
		System.arraycopy(appendBytes, 0, newB, 0, appendBytes.length);
		newB[newB.length - 1] = data;
		return newB;
	}
    
	public static byte[] getBytesFromLong(long longValueParm)
	{
		byte[] returnInt = new byte[8];
		returnInt[0] = (byte)((longValueParm >> 56) & 0xFF);
		returnInt[1] = (byte)((longValueParm >>> 48) & 0xFF);
		returnInt[2] = (byte)((longValueParm >>> 40) & 0xFF);
		returnInt[3] = (byte)((longValueParm >>> 32) & 0xFF);
		returnInt[4] = (byte)((longValueParm >>> 24) & 0xFF);
		returnInt[5] = (byte)((longValueParm >>> 16) & 0xFF);
		returnInt[6] = (byte)((longValueParm >>>  8) & 0xFF);
		returnInt[7] = (byte)((longValueParm >>>  0) & 0xFF);
		return returnInt;
	}
	
	public static void setBytesFromLong(long longValueParm, byte[] valuesParm,
			int offsetParm)
	{
		valuesParm[offsetParm + 0] = (byte)((longValueParm >> 56) & 0xFF);
		valuesParm[offsetParm + 1] = (byte)((longValueParm >>> 48) & 0xFF);
		valuesParm[offsetParm + 2] = (byte)((longValueParm >>> 40) & 0xFF);
		valuesParm[offsetParm + 3] = (byte)((longValueParm >>> 32) & 0xFF);
		valuesParm[offsetParm + 4] = (byte)((longValueParm >>> 24) & 0xFF);
		valuesParm[offsetParm + 5] = (byte)((longValueParm >>> 16) & 0xFF);
		valuesParm[offsetParm + 6] = (byte)((longValueParm >>>  8) & 0xFF);
		valuesParm[offsetParm + 7] = (byte)((longValueParm >>>  0) & 0xFF);
	}
	
	public static byte[] getBytesFromInt(int intValueParm)
	{
		byte[] returnInt = new byte[4];
		returnInt[0] = (byte)((intValueParm >>> 24) & 0xFF);
		returnInt[1] = (byte)((intValueParm >>> 16) & 0xFF);
		returnInt[2] = (byte)((intValueParm >>>  8) & 0xFF);
		returnInt[3] = (byte)((intValueParm >>>  0) & 0xFF);
		return returnInt;
	}
	
	public static void setBytesFromInt(int intValueParm, byte[] valuesParm,
			int offsetParm)
	{
		valuesParm[offsetParm + 0] = (byte)((intValueParm >>> 24) & 0xFF);
		valuesParm[offsetParm + 1] = (byte)((intValueParm >>> 16) & 0xFF);
		valuesParm[offsetParm + 2] = (byte)((intValueParm >>>  8) & 0xFF);
		valuesParm[offsetParm + 3] = (byte)((intValueParm >>>  0) & 0xFF);
	}
	
	public static int getIntFromBytes(byte[] valueBytesParm)
	{
		int returnInt = -1;
		try
		{
			int ch1 = (valueBytesParm[0] & 0xFF);
			int ch2 = (valueBytesParm[1] & 0xFF);
			int ch3 = (valueBytesParm[2] & 0xFF);
			int ch4 = (valueBytesParm[3] & 0xFF);
			returnInt = ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return returnInt;
	}
    
    public static int getIntFromBytes(byte[] valueBytesParm, int offsetParm)
    {
        int returnInt = -1;
        try
        {
            int ch1 = (valueBytesParm[offsetParm+0] & 0xFF);
            int ch2 = (valueBytesParm[offsetParm+1] & 0xFF);
            int ch3 = (valueBytesParm[offsetParm+2] & 0xFF);
            int ch4 = (valueBytesParm[offsetParm+3] & 0xFF);
            returnInt = ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        return returnInt;
    }
}