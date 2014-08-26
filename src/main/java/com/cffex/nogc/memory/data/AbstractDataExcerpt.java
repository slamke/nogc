/**
 *@author  Tao Zhou
*@classname AbstractDataExcerpt.java
*@date 下午4:38:15
*@description
 */
package com.cffex.nogc.memory.data;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.cffex.nogc.memory.NoGcByteBuffer;
import com.cffex.nogc.memory.Segment;
import com.cffex.nogc.memory.SegmentExcerpt;
import com.cffex.nogc.memory.buffer.Buffer;
import com.cffex.nogc.memory.data.DataOperateable;
import com.cffex.nogc.memory.data.exception.DataException;

/**
 * @author zhou
 *
 */
public abstract class AbstractDataExcerpt implements DataOperateable {

	protected SegmentExcerpt segmentExcerpt;
	protected Data data;
	protected Map<Thread, Integer> readingThreads = new HashMap<Thread, Integer>();
	protected int writeAccesses    = 0;
	protected int writeRequests    = 0;
	protected Thread writingThread = null;
	protected DataReadWriteLock lock;
	ReadWriteLock conLock = new ReentrantReadWriteLock(false); 
	public AbstractDataExcerpt(SegmentExcerpt segmentExcerpt, NoGcByteBuffer nogcData){
		int capacity = Segment.DEFAULT_CAPACITY - Buffer.CAPACITY;
		//NoGcByteBuffer noGcData = new NoGcByteBuffer(Buffer.CAPACITY, capacity, segmentExcerpt.getSegment().getByteBuffer());
		this.data = new Data(capacity, capacity, 0, 0, 0,nogcData);
		this.segmentExcerpt = segmentExcerpt;
	}
	public AbstractDataExcerpt(SegmentExcerpt segmentExcerpt, Data data){
		this.segmentExcerpt = segmentExcerpt;
		this.data = data;
	}
	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.data.DataOperateable#tryReadLock()
	 */
	@Override
	public final boolean tryReadLock() {
//		try {
//			lock.lockRead();
//		} catch (InterruptedException | DataException e) { 
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		//conLock.readLock().lock();
		return lock.SpinlockRead();
	}

	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.data.DataOperateable#unLockReadLock(java.lang.Thread)
	 */
	@Override
	public void unLockReadLock() {
		lock.unSpinlockRead();;
		//conLock.readLock().unlock();

	}

	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.data.DataOperateable#tryWriteLock()
	 */
	@Override
	public final void tryWriteLock() {
		try {
			lock.lockWrite();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//conLock.writeLock().lock();
	}

	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.data.DataOperateable#unlockWriteLock()
	 */
	@Override
	public final void unLockWriteLock() {
		try {
			lock.unlockWrite();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//conLock.writeLock().unlock();
	}
	
	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.data.DataOperateable#getById(long)
	 */
	@Override
	public final byte[] getById(long id) {
		// TODO Auto-generated method stub
		return getDataById(id);
	}
	
	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.data.DataOperateable#getPropertyById(long, int)
	 */
	@Override
	public byte[] getPropertyById(long id, int index) {
		// TODO Auto-generated method stub
		return getDataPropertyById(id, index);
	}
	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.data.DataOperateable#getDataWithIdRange(long, long)
	 */
	@Override
	public final byte[] getDataWithIdRange0(long minId, long maxId) {
		// TODO Auto-generated method stub
		return getData0(minId, maxId);

	}
	
	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.data.DataOperateable#getDataWithIdRange(long, long)
	 */
	@Override
	public final HashMap<Long, byte[]> getDataWithIdRange(long minId, long maxId) {
		// TODO Auto-generated method stub
		return getData(minId, maxId);

	}

	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.data.DataOperateable#insertDataWithIdRange(byte[], byte[], long, long)
	 */
	@Override
	public final void insertDataWithIdRange(byte[] dataBytes, byte[] indexBytes, long minId,
			long maxId) {
		// TODO Auto-generated method stub
		insertData(dataBytes, indexBytes, minId, maxId);
	}
	
	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.data.DataOperateable#insertDataWithIdRange(byte[], byte[], long, long)
	 */
	@Override
	public final void insertDataWithIdRange(byte[] dataBytes, IndexItem[] indexItems, long minId,
			long maxId) {
		// TODO Auto-generated method stub
		insertData(dataBytes, indexItems, minId, maxId);
	}
	
	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.data.DataOperateable#insertData(byte[], byte[], boolean)
	 */
	@Override
	public void insertData(byte[] dataBytes, IndexItem[] indexItems, long miId, long maxId, boolean readonly) {
		// TODO Auto-generated method stub
		if(readonly){
			insertData(dataBytes, indexItems, miId, maxId);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.data.DataOperateable#insertData(byte[], byte[], boolean)
	 */
	@Override
	public void insertData(byte[] dataBytes,  byte[] indexBytes , long miId, long maxId, boolean readonly) {
		// TODO Auto-generated method stub
		if(readonly){
			insertData(dataBytes, indexBytes, miId, maxId);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.data.DataOperateable#resize()
	 */
	@Override
	public final SegmentExcerpt resize() {
		// TODO Auto-generated method stub
		resizeData();
		return null;
	}
	
	
	
	
	/*
	 * 在Index区二分查找
	 * @param id 查找的id
	 * @return offset的值
	 */
	protected abstract int getOffsetById(long id);
		
	protected abstract byte[] getDataById(long id);
	
	protected abstract byte[] getDataPropertyById(long id, int index);
	
	/*
	 * index(id+offset)
	 * 找到index中id对应offset的位置
	 */
	protected abstract int binarySearchById(long id);
	
	/*
	 * 找到id插入的合适位置
	 */
	protected abstract int findIdOffset(long id);

	//object数组id,index,id,index
	protected abstract Object[] GetIndexRegion(long minid, long maxid);
	protected abstract int setIndexRegion();
	protected abstract int checkFreeSpace();
	protected abstract SegmentExcerpt resizeData();
	protected abstract void insertData(byte[] dataBytes, byte[] indexBytes, long minId, long maxId);
	protected abstract void insertData(byte[] dataBytes, IndexItem[] indexItems, long minId, long maxId);
	protected abstract byte[] getData0(long minId, long maxId);
	protected abstract HashMap<Long, byte[]> getData(long minId, long maxId);
//	protected abstract void lockRead();
//	protected abstract void unlockRead() throws InterruptedException;
//	protected abstract void lockWrite();
//	protected abstract void unlockWrite() throws InterruptedException;
	
//	private static ReadWriteLock lock = new ReentrantReadWriteLock();  
//	private void getLock(){
//		lock.readLock().lock();  
//	}
	
	
}
