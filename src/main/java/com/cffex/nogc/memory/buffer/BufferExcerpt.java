package com.cffex.nogc.memory.buffer;

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
}
