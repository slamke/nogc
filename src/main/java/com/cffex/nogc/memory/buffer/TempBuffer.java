/**
 * @time 2014年8月20日
 * @author sunke
 */
package com.cffex.nogc.memory.buffer;

import java.nio.ByteBuffer;
import java.util.List;

import com.cffex.nogc.cson.core.utils.Tuple;
import com.cffex.nogc.memory.SegmentExcerpt;

/**
 * @author sunke
 * @ClassName TempBuffer
 * @Description: TempBuffer为buffer的拷贝版本，用于merge
 */
public class TempBuffer{
	
	private List<BufferLog> insertIndexList;
	private List<BufferLog> updateIndexList;
	private long minId;
	private long maxId;
	private ByteBuffer data;
	private SegmentExcerpt segmentExcerpt;
	public TempBuffer(ByteBuffer buffer,SegmentExcerpt segmentExcerpt){
		this.data = buffer;
		this.segmentExcerpt = segmentExcerpt;
	}
	
	/**
	 * 遍历data中的内容，计算最大Id和最小Id，形成未处理的insertIndexList和updateIndexList
	 * @return Tuple<Long, Long>-->(minId,maxId)
	 */
	public Tuple<Long, Long>preperation(){
		return null;
	}
	
	/**
	 * 对未处理的insertIndexList和updateIndexList进行排序和归并处理
	 */
	public void constructIndexList(){
		
	}
}
