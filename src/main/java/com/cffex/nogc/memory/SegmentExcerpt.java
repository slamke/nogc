package com.cffex.nogc.memory;

import java.util.Iterator;
import java.util.List;

import sun.nio.ch.DirectBuffer;

import com.cffex.nogc.enumeration.IsolationType;
import com.cffex.nogc.memory.buffer.Buffer;
import com.cffex.nogc.memory.buffer.BufferExcerpt;
import com.cffex.nogc.memory.buffer.BufferLog;
import com.cffex.nogc.memory.buffer.BufferOperatable;
import com.cffex.nogc.memory.buffer.BufferLog.BufferLogType;
import com.cffex.nogc.memory.data.Data;
import com.cffex.nogc.memory.data.DataExcerpt;
import com.cffex.nogc.memory.data.DataOperateable;
import com.cffex.nogc.memory.utils.MemoryTool;

/**
 * SegmentExcerpt
 * @author sunke
 * @ClassName SegmentExcerpt
 * @Description: SegmentExcerpt实现了SegmentOperateable接口，提供了段操作的真实实现
 */
public class SegmentExcerpt extends AbstractSegmentExcerpt {
	/**
	 * 隔离级别 
	 */
	private IsolationType isolationType;
	/**
	 * 段数据
	 */
	private Segment segment;

	


	private BufferOperatable bufferOperatable;
	
	private DataOperateable dataOperateable;
	
	/**
	 * @param isolationType 隔离级别 
	 * @param segment 段数据
	 */
	public SegmentExcerpt(IsolationType isolationType) {
		super();
		DirectBuffer directBuffer = MemoryTool.allocate(Segment.DEFAULT_CAPACITY);
		segment = new Segment(directBuffer);
		NoGcByteBuffer nogcData = new NoGcByteBuffer(Data.OFFSET, Segment.DEFAULT_CAPACITY - Buffer.CAPACITY,
				directBuffer);
		NoGcByteBuffer nogcBuffer = new NoGcByteBuffer(Buffer.OFFSET, Buffer.CAPACITY,
				directBuffer);
		this.isolationType = isolationType;
		this.dataOperateable = new DataExcerpt(this,nogcData);
		this.bufferOperatable = new BufferExcerpt(this,nogcBuffer);
	}
	
	public Segment getSegment() {
		return segment;
	}


	/**
	 * 将buffer区获取的operation与data区中的数据进行merge
	 * @return merge后的结果
	 */
	private byte[] mergeDataWithOperation(){
		return null;
	}

	public void setReadonly(boolean readonly){
		segment.setReadonly(readonly);
	}
	public boolean isReadonly() {
		return segment.isReadonly();
	}
	public DataOperateable getDataOperateable(){
		return null;
	}
	/**
	 * TODO MERGE时，根据隔离级别设置lock
	 */
	public void mergeCallback(){}
	
	protected boolean addBufferLog(BufferLog bufferLog){
		return this.bufferOperatable.appendOperation(bufferLog);
	}
	protected void release(){
		this.getSegment().release();
	}
	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.AbstractSegmentExcerpt# isDataCompleteInBuffer(long)
	 */
	@Override
	protected boolean isDataCompleteInBuffer(List<BufferLog> bufferList){
//		INSERT(0),
//		DELETE(1),
//		UPDATE_ALL(2),
//		UPDATE_PROPERTY(3);
//		如果有DALETE、INSERT、UPDSTE_ALL那么buffer中的数据时完整的
		for(BufferLog bLog : bufferList){
			if(bLog.getFlag().equals(BufferLogType.DELETE)||
					bLog.getFlag().equals(BufferLogType.INSERT)||
					bLog.getFlag().equals(BufferLogType.UPDATE_ALL)){
				return true;
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.AbstractSegmentExcerpt#isDataPropertyCompleteInBuffer(java.util.List)
	 */
	@Override
	protected boolean isDataPropertyCompleteInBuffer(
			List<BufferLog> bufferList, int index) {
		// TODO Auto-generated method stub
		//如果有对这个id进行过
		//DELETE操作，INSERT操作，UPDATE_ALL操作
		//或者是对这个id的这个index的值做过更新即UPDATE_PROPERTY操作，那么buffer中的值是最新的
		for(BufferLog bLog : bufferList){
			if(bLog.getFlag().equals(BufferLogType.DELETE)||
					bLog.getFlag().equals(BufferLogType.INSERT)||
					bLog.getFlag().equals(BufferLogType.UPDATE_ALL)||
					(bLog.getFlag().equals(BufferLogType.UPDATE_PROPERTY)&&bLog.getIndex() == index)){
				return true;
			}
		}
		return false;
	}
	

	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.AbstractSegmentExcerpt#findItemInBuffer(long)
	 */
	@Override
	protected List<BufferLog> findItemInBuffer(long id) {
		// TODO Auto-generated method stub
		return this.bufferOperatable.getById(id);
	}

	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.AbstractSegmentExcerpt#findItemInData(long)
	 */
	@Override
	protected byte[] findItemInData(long id) {
		// TODO Auto-generated method stub
		return this.dataOperateable.getById(id);
	}
	
	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.AbstractSegmentExcerpt#findItemPropertyInData(long, int)
	 */
	@Override
	protected byte[] findItemPropertyInData(long id, int index) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.AbstractSegmentExcerpt#mergeData(byte[], byte[])
	 */
	@Override
	protected byte[] mergeData(byte[] bufferValue, byte[] dataValue) {
		// TODO Auto-generated method stub
		return null;
	}



	
}
