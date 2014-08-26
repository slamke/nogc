package com.cffex.nogc.memory.data;

import java.util.HashMap;

import com.cffex.nogc.memory.SegmentExcerpt;
import com.sun.javafx.collections.MappingChange.Map;

/**
 * @author Tao Zhou
 * @ClassName DataOperateable
 * @Description: Data接口
 */
public interface DataOperateable {
	/**
	 * @param callingThread
	 * @return
	 */
	public boolean tryReadLock();
	/**
	 * @param callingThread
	 * @return
	 */
	public void unLockReadLock();
	/**
	 * @param callingThread
	 * @return
	 */
	void tryWriteLock();
	/**
	 * @param callingThread
	 * @return
	 */
	public void unLockWriteLock();
	/**
	 * @param id
	 * @return
	 */
	public byte[] getById(long id);
	
	/**
	 * @param id
	 * @param index
	 * @return
	 */
	public byte[] getPropertyById(long id, int index);
	/**
	 * @param minId
	 * @param maxId
	 * @return
	 */
	public byte[] getDataWithIdRange0(long minId, long maxId);
	
	public HashMap<Long, byte[]> getDataWithIdRange(long minId, long maxId);

	/**
	 * @param data
	 * @param index
	 * @param minId
	 * @param maxId
	 * @return
	 */
	public int insertDataWithIdRange(byte[] data, byte[] index, long minId,
			long maxId);
	public int insertData(byte[] newData, Object[] newIndex, long miId, long maxId, boolean readonly);
	public SegmentExcerpt resize();
	
}
