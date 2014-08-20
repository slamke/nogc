package com.cffex.nogc.memory.buffer;

import java.util.List;

/**
 * 
 * @author sunke
 * @ClassName SegmentExcerpt
 * @Description: TODO
 */
public interface BufferOperatable {
	
	public boolean appendOperation(BufferLog log);

	public boolean tryLockWithLength(int length);

	public List<BufferLog> getById(long id);

	public byte[] getPropertyById(long id, int index);
}
