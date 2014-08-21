/**
 *@author  Tao Zhou
*@classname AbstractDataExcerpt.java
*@date 下午4:38:15
*@description
 */
package com.cffex.nogc.memory.data;

import com.cffex.nogc.memory.NoGcByteBuffer;
import com.cffex.nogc.memory.Segment;
import com.cffex.nogc.memory.SegmentExcerpt;
import com.cffex.nogc.memory.buffer.Buffer;
import com.cffex.nogc.memory.data.DataOperateable;

/**
 * @author zhou
 *
 */
public abstract class AbstractDataExcerpt implements DataOperateable {

	protected SegmentExcerpt segmentExcerpt;
	protected Data data;
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
	public final int tryReadLock() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.data.DataOperateable#getById(long)
	 */
	@Override
	public final byte[] getById(long id) {
		// TODO Auto-generated method stub
		getDataById(id);
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.data.DataOperateable#tryWriteLock()
	 */
	@Override
	public final int tryWriteLock() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.data.DataOperateable#unlockWriteLock()
	 */
	@Override
	public final int unlockWriteLock() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.data.DataOperateable#getDataWithIdRange(long, long)
	 */
	@Override
	public final byte[] getDataWithIdRange(long minId, long maxId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.data.DataOperateable#insertDataWithIdRange(byte[], byte[], long, long)
	 */
	@Override
	public final int insertDataWithIdRange(byte[] data, byte[] index, long minId,
			long maxId) {
		// TODO Auto-generated method stub
		
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.data.DataOperateable#insertData(byte[], byte[], boolean)
	 */
	@Override
	public int insertData(byte[] data, byte[] index, boolean readonly) {
		// TODO Auto-generated method stub
		return 0;
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
	protected abstract int insertDataWithIdRangexxx(byte[] newData, byte[] newIndex, long minId, long maxId);
	protected abstract byte[] getDataWithIdRangexxx(long minId, long maxId);
	

}
