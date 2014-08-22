/**
 * @time 2014年8月21日
 * @author sunke
 */
package com.cffex.nogc.memory.buffer;

import java.util.ArrayList;

import com.cffex.nogc.memory.SegmentExcerpt;
import com.cffex.nogc.memory.buffer.BufferLog.BufferLogType;

/**
 * @author sunke
 * @ClassName MergeTask
 * @Description: TODO 
 */
public class MergeTask {
	private final TempBuffer tempBuffer;
	private final SegmentExcerpt segmentExcerpt;
	/**
	 * @param tempBuffer
	 * @param segmentExcerpt
	 */
	public MergeTask(TempBuffer tempBuffer, SegmentExcerpt segmentExcerpt) {
		super();
		this.tempBuffer = tempBuffer;
		this.segmentExcerpt = segmentExcerpt;
		
	}
	private void writeBack(){}
	private void mergeUpdate(){}
	private void mergeInsert(){}
	private void dataPreOperation(){}
	private void bufferPreOperation(){}
	private void preOperation(){}
	public TempBuffer merge(){
		//tempBuffer.getNoGcByteBuffer();
		BufferLog log1 = new BufferLog(BufferLogType.INSERT, 1, null);
		BufferLog log2 = new BufferLog(BufferLogType.INSERT, 2, null);
		BufferLog log3 = new BufferLog(BufferLogType.INSERT, 3, null);
		ArrayList<BufferLog> insertIndexList = new ArrayList<BufferLog>();
		insertIndexList.add(log1);
		insertIndexList.add(log2);
		insertIndexList.add(log3);
		BufferLog u1 = new BufferLog(BufferLogType.UPDATE_ALL, 4, null);
		BufferLog u2 = new BufferLog(BufferLogType.UPDATE_ALL, 5, null);
		BufferLog u3 = new BufferLog(BufferLogType.UPDATE_ALL, 6, null);
		ArrayList<BufferLog> updateIndexList = new ArrayList<BufferLog>();
		return new TempBuffer(insertIndexList, updateIndexList, 1, 6);
	}
}
