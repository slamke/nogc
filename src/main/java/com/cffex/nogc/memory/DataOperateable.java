package com.cffex.nogc.memory;

public interface DataOperateable {
	public int insertDataWithIdRange();
	public int tryReadLock();
	public byte[] getById();
	public int tryWriteLock();
	public int unlockWriteLock();
	public byte[] getDataWithIdRange();
}
