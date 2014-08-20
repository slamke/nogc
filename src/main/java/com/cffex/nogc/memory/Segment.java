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
	
	/**
	 * 目前segment是不是只读状态(默认true：只有insert操作；遇见delete和update后，变为false)
	 * readonly变为false后，进行merge时需要对data区中的数据进行排序
	 */
	private boolean readonly;
	
//	/**
//	 * segment的容量
//	 */
//	private int capacity;
	
	/**
	 * segment的地址空间，通过byteBuffer指向堆外内存
	 */
	private ByteBuffer byteBuffer;
	
	/**
	 * byteBuffer的起始地址 
	 */
	private long startAddress;
	
	public Segment(){
		super();
		this.byteBuffer = MemoryTool.allocate(DEFAULT_CAPACITY);
		startAddress = ((DirectBuffer) byteBuffer).address();
		readonly = true;
	}
	
	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	public long getStartAddress(){
		return startAddress;
	}

//	/**
//	 * @return
//	 */
//	public int getCapactiy() {
//		return this.capacity;
//	}
}
