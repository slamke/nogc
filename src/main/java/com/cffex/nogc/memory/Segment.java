package com.cffex.nogc.memory;
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
	
}
