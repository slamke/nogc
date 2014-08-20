package com.cffex.nogc.memory.data;
/**
 * @author Tao Zhou
 * @ClassName DataOperateable
 * @Description: Data接口
 */
public interface DataOperateable {
	public int insertDataWithIdRange(byte[] b, long minid, long maxid);
	public int tryReadLock();
	public byte[] getById(long id);
	public int tryWriteLock();
	public int unlockWriteLock();
	public byte[] getDataWithIdRange(long minid, long maxid);
}
