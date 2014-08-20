package com.cffex.nogc.memory.buffer;

import java.util.List;

import com.cffex.nogc.memory.SegmentOperateable;

/**
 * 
 * @author sunke
 * @ClassName SegmentExcerpt
 * @Description: TODO
 */
public class BufferExcerpt implements BufferOperatable{
	private Buffer buffer;
	private SegmentOperateable segmentOperateable;
	public BufferExcerpt(SegmentOperateable segmentOperateable){
		this.segmentOperateable = segmentOperateable;
	}
	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.buffer.BufferOperatable#appendOperation(com.cffex.nogc.memory.buffer.BufferLog)
	 */
	@Override
	public boolean appendOperation(BufferLog log) {
		// TODO Auto-generated method stub
		return false;
	}
	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.buffer.BufferOperatable#tryLockWithLength(int)
	 */
	@Override
	public boolean tryLockWithLength(int length) {
		// TODO Auto-generated method stub
		return false;
	}
	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.buffer.BufferOperatable#getById(long)
	 */
	@Override
	public List<BufferLog> getById(long id) {
		// TODO Auto-generated method stub
		return null;
	}
	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.buffer.BufferOperatable#getPropertyById(long, int)
	 */
	@Override
	public byte[] getPropertyById(long id, int index) {
		// TODO Auto-generated method stub
		return null;
	}
}
