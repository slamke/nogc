package com.cffex.nogc.memory;

import com.cffex.nogc.enumeration.IsolationType;
import com.cffex.nogc.memory.SegmentOperateable;
import com.cffex.nogc.memory.buffer.BufferExcerpt;
import com.cffex.nogc.memory.buffer.BufferOperatable;
import com.cffex.nogc.memory.data.DataExcerpt;
import com.cffex.nogc.memory.data.DataOperateable;

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
	private Segment Segment;

	
	private BufferOperatable bufferOperatable;
	
	private DataOperateable dataOperateable;
	
	/**
	 * @param isolationType 隔离级别 
	 * @param segment 段数据
	 */
	public SegmentExcerpt(IsolationType isolationType,
			Segment segment) {
		super();
		this.isolationType = isolationType;
		Segment = segment;
		
		this.dataOperateable = new DataExcerpt(this);
		this.bufferOperatable = new BufferExcerpt(this);
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
	

}
