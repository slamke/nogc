package com.cffex.nogc.memory.buffer;

import java.util.List;

import com.cffex.nogc.memory.SegmentExcerpt;
import com.cffex.nogc.memory.SegmentOperateable;

/**
 * 
 * @author sunke
 * @ClassName BufferExcerpt
 * @Description: buffer操作接口的真实实现类， 包含了具体的底层操作原语
 */
public class BufferExcerpt implements BufferOperatable{
	
	private Buffer buffer;
	private SegmentExcerpt segmentExcerpt;
	
	public BufferExcerpt(SegmentExcerpt segmentExcerpt){
		this.segmentExcerpt = segmentExcerpt;
	}
	
	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.buffer.BufferOperatable#appendOperation(com.cffex.nogc.memory.buffer.BufferLog)
	 */
	@Override
	public final boolean appendOperation(BufferLog log) {
		try {
			int length = log.getLength();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
		return false;
	}
	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.buffer.BufferOperatable#tryLockWithLength(int)
	 */
	@Override
	public final boolean tryLockWithLength(int length) {
		boolean lockResult = lock();
		if (lockResult) {
			checkMerge(length);
		}
		return lockResult;
	}
	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.buffer.BufferOperatable#getById(long)
	 */
	@Override
	public final List<BufferLog> getById(long id) {
		// TODO Auto-generated method stub
		return null;
	}
	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.buffer.BufferOperatable#getPropertyById(long, int)
	 */
	@Override
	public final byte[] getPropertyById(long id, int index) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void updateLength(){
		
	}
	private void append(){}
	private boolean lock(){return false;}
	private boolean checkFreeSpace(){return false;}
	private boolean swapDataAndMarkFree(){return false;}
	private boolean unlock(){return false;}
	private boolean checkMerge(int length){return false;}
}
