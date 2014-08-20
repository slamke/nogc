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
	@Override
	public final boolean appendOperation(BufferLog log) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public final boolean tryLockWithLength(int length) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public final List<BufferLog> getById(long id) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public final byte[] getPropertyById(long id, int index) {
		// TODO Auto-generated method stub
		return null;
	}
}
