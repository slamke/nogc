/**
 * @time 2014年8月21日
 * @author sunke
 */
package com.cffex.nogc.memory.buffer;

import com.cffex.nogc.memory.SegmentExcerpt;

/**
 * @author sunke
 * @ClassName MergeTask
 * @Description: TODO 
 */
public class MergeTask {
	private TempBuffer tempBuffer;
	private SegmentExcerpt segmentExcerpt;
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
	public void merge(){}
}
