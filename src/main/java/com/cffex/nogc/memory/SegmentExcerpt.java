package com.cffex.nogc.memory;

import sun.nio.ch.DirectBuffer;

import com.cffex.nogc.enumeration.IsolationType;
import com.cffex.nogc.memory.buffer.Buffer;
import com.cffex.nogc.memory.buffer.BufferExcerpt;
import com.cffex.nogc.memory.buffer.BufferOperatable;
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
public class SegmentExcerpt implements SegmentOperateable {
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
	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.SegmentOperateable#insertItem(long, byte[])
	 */
	@Override
	public final boolean insertItem(long id, byte[] value) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.SegmentOperateable#deleteItem(long)
	 */
	@Override
	public final boolean deleteItem(long id) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.SegmentOperateable#getItem(long)
	 */
	@Override
	public final byte[] getItem(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.SegmentOperateable#getItemProperty(long, int)
	 */
	@Override
	public final byte[] getItemProperty(long id, int index) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.SegmentOperateable#updateItem(long, byte[])
	 */
	@Override
	public final boolean updateItem(long id, byte[] newValue) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.SegmentOperateable#updateItemProperty(long, int, byte[])
	 */
	@Override
	public final boolean updateItemProperty(long id, int index, byte[] newValue) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.SegmentOperateable#freeSegment()
	 */
	@Override
	public final boolean freeSegment() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.SegmentOperateable#deleteIdRange(long, long)
	 */
	@Override
	public final boolean deleteIdRange(long minId, long maxId) {
		// TODO Auto-generated method stub
		return false;
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
}
