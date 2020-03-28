package top.hserver.core.event.queue;


import lombok.extern.slf4j.Slf4j;
import top.hserver.core.event.queue.util.RAcsFile;
import top.hserver.core.event.queue.util.Utilities;

import java.io.File;
import java.io.RandomAccessFile;


@Slf4j
public class FilePersistence extends BasePersistence
{
	private String directory;
	private RAcsFile theWriteDataFile;
	private RAcsFile theReadDataFile;
	private RAcsFile theFetchPosiFile;
	private boolean forceToDisk = true;
	private long curFetchPosi;
	private final static String DataFile_Name = "dataFile.data";
	private final static String FetchPosiFile_Name = "fetchPosiFile.data";
	
	public FilePersistence(long maxByteArray_SzParm,
			int oneBatchWriteCntParm, int isCanReleaseResMaxTimeParm,
			String directoryParm) throws Exception
	{
		super(maxByteArray_SzParm,
			oneBatchWriteCntParm, isCanReleaseResMaxTimeParm);
		directory = directoryParm;
		if (! directoryParm.endsWith("/"))
		{
			directory = directory + "/";
			//throw new SpongeException("文件目录设置不对,必须以'/'结尾 " + directoryParm);
		}
		theWriteDataFile = new RAcsFile(directory + DataFile_Name);
		theReadDataFile = new RAcsFile(directory + DataFile_Name, "r");
		theFetchPosiFile = new RAcsFile(directory + FetchPosiFile_Name);
		initCurFetchPosi();
		theWriteDataFile.getDataFile().seek(theWriteDataFile.getFileLength());
		//readOneBatch_MaxBytes = new byte[readOneBatch_MaxByteSz];
	}
	
	private void initCurFetchPosi()
	{
		try
		{
			if (theFetchPosiFile.getFileLength() == 8)
			{
				curFetchPosi = theFetchPosiFile.getDataFile().readLong();
			}
			
			theReadDataFile.getDataFile().seek(curFetchPosi);
			
			if (curFetchPosi < theReadDataFile.getFileLength())
			{
				setHaveDataInPersistence(true);
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	@Override
	public byte[] doFetchOneBatchBytes() throws Exception
	{
		byte[] tmpLengthByte = new byte[6];
		int tmpReadCnt = theReadDataFile.getDataFile().read(tmpLengthByte);
		if (tmpReadCnt == -1)
		{
			return null;
		}
		
		int tmpByteLength = Utilities.getIntFromBytes(tmpLengthByte, 2);
		byte[] tmpReadBytes = new byte[tmpByteLength];
		
		tmpReadCnt = theReadDataFile.getDataFile().read(tmpReadBytes, 6, tmpByteLength - 6);
		if (tmpReadCnt == -1)
		{
			return null;
		}
		else
		{
			curFetchPosi += tmpByteLength;
			byte[] tmpBytes = Utilities.getBytesFromLong(curFetchPosi);
			theFetchPosiFile.getDataFile().seek(0);
			theFetchPosiFile.getDataFile().write(tmpBytes);
			theFetchPosiFile.getDataFile().getFD().sync();
			return tmpReadBytes;
		}
	}

	@Override
	public void doWriteOneBatchBytes(byte[] writeBytesParm, int offsetParm, int lengthParm)
			throws Exception
	{
		long tmpStartTime = System.currentTimeMillis();
		
		RandomAccessFile file = theWriteDataFile.getDataFile();
		file.write(writeBytesParm, offsetParm, lengthParm);
        
        if (forceToDisk) {
            file.getFD().sync();
        }
        
        log.debug("一次写入耗时 {}",(System.currentTimeMillis() - tmpStartTime));
	}

	@Override
	public void destroy() throws Exception
	{
		theWriteDataFile.close();
		theReadDataFile.close();
	}

	@Override
	public void doWriteOneBatchBytes(byte[] writeBytesParm) throws Exception
	{
		doWriteOneBatchBytes(writeBytesParm, 0, writeBytesParm.length);
	}

	@Override
	public void canReleaseRes() throws Exception
	{
		if (curFetchPosi != 0)
		{
			theWriteDataFile.destroy();
			theReadDataFile.destroy();
			
			theWriteDataFile = null;
			theReadDataFile = null;
			
			File tmpFile = new File(directory + DataFile_Name);
			deleteFile(tmpFile);
			
			curFetchPosi = 0;
			theFetchPosiFile.getDataFile().seek(0);
			theFetchPosiFile.getDataFile()
				.write(Utilities.getBytesFromLong(curFetchPosi));
			theFetchPosiFile.getDataFile().getFD().sync();
			
			theWriteDataFile = new RAcsFile(directory + DataFile_Name);
			theReadDataFile = new RAcsFile(directory + DataFile_Name, "r");
		}
	}
	
	private boolean deleteFile(File fileToDelete)
	{
        if (fileToDelete == null || !fileToDelete.exists()) {
            return true;
        }
        boolean result = fileToDelete.delete();
        return result;
    }
}
