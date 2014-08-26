/**
 * @time 2014年8月21日
 * @author sunke
 */
package com.cffex.nogc.memory.buffer.merge;

import java.util.ArrayList;

import com.cffex.nogc.memory.SegmentExcerpt;
import com.cffex.nogc.memory.buffer.BufferLog;
import com.cffex.nogc.memory.buffer.BufferLog.BufferLogType;

/**
 * @author sunke
 * @ClassName MergeTask
 * @Description: MergeTask为不可变的Pojo-->原因：MergeTask作为变量在actor模型框架akka中进行传递，actor要求传递的msg为不可变的 
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
	/**
	 * 在获取data的进程或者actor中调用，以获取data区中的数据
	 */
	public void dataPreOperation(){
		if (tempBuffer.isReady()) {
			segmentExcerpt.getDataOperateable().getDataWithIdRange(tempBuffer.getMinId(), tempBuffer.getMaxId());
		}
	}
	private void bufferPreOperation(){}
	
	public TempBuffer getTempBuffer() {
		return tempBuffer;
	}
	public SegmentExcerpt getSegmentExcerpt() {
		return segmentExcerpt;
	}
	/**
	 * 对task中的buffer进行预遍历，获取新的tempbuffer（包含有minId和maxId）
	 * @return
	 */
	public TempBuffer preTraversalOperation(){
		return tempBuffer.preperation();
	}
	public TempBuffer merge(){
		//tempBuffer.getNoGcByteBuffer();
		
		return null;
	}
}
