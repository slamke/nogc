package com.cffex.nogc.memory;

import java.nio.ByteBuffer;
import sun.nio.ch.DirectBuffer;
import com.cffex.nogc.memory.utils.MemoryTool;

/**
 * @author sunke TaoZhou
 * @ClassName SegmentExcerpt
 * @Description: 基于hash的段类型数据
 */
public class Segment {
	/**
	 * 默认段大小 4M
	 */
	public final static int DEFAULT_CAPACITY = 4*1024*1024;
	
	/**
	 * 默认扩容参数
	 */
	public final static float DEFAULT_REACTOR = 1.2f;
	
	
	private int capacity;
	private ByteBuffer address;
	
	public Segment(){
		super();
		this.address = MemoryTool.allocate(DEFAULT_CAPACITY);
		this.capacity = DEFAULT_CAPACITY;
		//print something
		int temp_adderss = (int) ((DirectBuffer) address).address();
		System.out.println(temp_adderss);
	}

	/**
	 * @return
	 */
	public int getCapactiy() {
		return this.capacity;
	}
}
