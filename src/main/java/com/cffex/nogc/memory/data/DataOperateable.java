package com.cffex.nogc.memory.data;
/**
 * @author Tao Zhou
 * @ClassName DataOperateable
 * @Description: Data接口
 */
public interface DataOperateable {
	public int tryReadLock();
	public byte[] getById(long id);
	public int tryWriteLock();
	public int unlockWriteLock();
	/**
	 * @param minId
	 * @param maxId
	 * @return
	 */
	public byte[] getDataWithIdRange(long minId, long maxId);

	/**
	 * @param data
	 * @param index
	 * @param minId
	 * @param maxId
	 * @return
	 */
	int insertDataWithIdRange(byte[] data, byte[] index, long minId,
			long maxId);
}
