package com.cffex.nogc.memory;

import java.nio.ByteBuffer;

import com.cffex.nogc.memory.buffer.BufferOperatable;

/**
 * @author sunke
 * @ClassName SegmentExcerpt
 * @Description: 基于hash的段类型数据
 */
public class Segment {
	/**
	 * 默认段大小 4M
	 */
	public final static long DEFAULT_CAPACITY = 4*1024*1024;
	
	/**
	 * 默认扩容参数
	 */
	public final static float DEFAULT_REACTOR = 1.2f;
	
	
	private int capacity;
	private ByteBuffer address;
	private BufferOperatable bufferOperatable;
	private DataOperateable dataOperateable;
}
