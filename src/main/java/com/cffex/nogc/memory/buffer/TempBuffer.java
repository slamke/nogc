/**
 * @time 2014年8月20日
 * @author sunke
 */
package com.cffex.nogc.memory.buffer;

import java.util.ArrayList;
import java.util.List;

import com.cffex.nogc.cson.core.utils.Tuple;
import com.cffex.nogc.memory.NoGcByteBuffer;
import com.cffex.nogc.memory.buffer.BufferLog.BufferLogCusor;
import com.cffex.nogc.memory.buffer.BufferLog.BufferLogType;

/**
 * @author sunke
 * @ClassName TempBuffer
 * @Description: TempBuffer为buffer的拷贝版本，用于merge
 */
public class TempBuffer extends Buffer{
	
	private final List<BufferLog> insertIndexList;
	private final List<BufferLog> updateIndexList;
	private final long minId;
	private final long maxId;
	/**
	 * 构建尚未处理的TempBuffer
	 * @param noGcByteBuffer
	 */
	public TempBuffer(NoGcByteBuffer noGcByteBuffer){
		super(noGcByteBuffer);
		this.insertIndexList = null;
		this.updateIndexList = null;
		this.minId = -1;
		this.maxId = -1;
	}
	
	
	
	/**构造函数生成的数据是final的，在actor进行传递
	 * @param noGcByteBuffer
	 * @param insertIndexList
	 * @param updateIndexList
	 * @param minId
	 * @param maxId
	 */
	public TempBuffer(List<BufferLog> insertIndexList, List<BufferLog> updateIndexList,
			long minId, long maxId) {
		super(null);
		this.insertIndexList = insertIndexList;
		this.updateIndexList = updateIndexList;
		this.minId = minId;
		this.maxId = maxId;
	}



	/**
	 * 遍历data中的内容，计算最大Id和最小Id，形成未处理的insertIndexList和updateIndexList
	 * @return 新构建的TempBuffer
	 */
	public TempBuffer preperation(){
		BufferLogCusor cusor = new BufferLogCusor(noGcByteBuffer);
		List<BufferLog> unhandledInsert = new ArrayList<BufferLog>();
		List<BufferLog> unhandledUpdate = new ArrayList<BufferLog>();
		long maxId = Long.MIN_VALUE;
		long minId = Long.MAX_VALUE;
		while (cusor.hasNext()) {
			BufferLog log = cusor.next();
			if (log.getFlag() == BufferLogType.INSERT) {
				unhandledInsert.add(log);
			} else {
				unhandledUpdate.add(log);
			}
			minId = Math.min(minId, log.getId());
			maxId = Math.max(maxId, log.getId());
		}
		return new TempBuffer(unhandledInsert, unhandledUpdate, minId, maxId);
	}
	
	/**
	 * 对未处理的insertIndexList和updateIndexList进行排序和归并处理
	 */
	public TempBuffer constructIndexList(){
		return null;
	}
}
