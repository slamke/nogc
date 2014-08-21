package com.cffex.nogc.memory.buffer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import com.cffex.nogc.memory.NoGcByteBuffer;
import com.cffex.nogc.memory.SegmentExcerpt;
import com.cffex.nogc.memory.buffer.BufferLog.BufferLogCusor;
import com.cffex.nogc.memory.buffer.exception.BufferLogException;
import com.cffex.nogc.memory.utils.MemoryTool;

/**
 * @author sunke
 * @ClassName BufferExcerpt
 * @Description: buffer操作接口的真实实现类， 包含了具体的底层操作原语
 */
public class BufferExcerpt extends AbstractBufferExcerpt{
	
	private Buffer buffer;
	protected SegmentExcerpt segmentExcerpt;
	
	public BufferExcerpt(SegmentExcerpt segmentExcerpt,NoGcByteBuffer noGcByteBuffer){
		super(segmentExcerpt, noGcByteBuffer);
	}
	
	
	/**
	 * 更新buffer的长度
	 * @param length buffer长度的增量
	 * @return 长度增加后，可用长度相对于buffer的偏移量
	 */
	private int updateLength(int length){
		return buffer.updateLengthWithIncrement(length);
	}
	
	private void append(BufferLog log,int startLength){
		ByteBuffer byteBuffer = log.toBytebuffer();
		buffer.writeBytes(byteBuffer.array(), startLength);
	}
	
	private boolean lock(){
		// TODO
		do {
            v = length.get();
        }while (!length.compareAndSet(v, v + increment));
		return false;
	}
	
	/**
	 * 检测新增length长度的数据后，是否超界限
	 * @param length 新增length长度的数据
	 * @return true-->OK false --> need merge
	 */
	private boolean checkFreeSpace(int length){
		int has = buffer.getLength();
		int freeSpace = Buffer.CAPACITY - length - has;
		if (freeSpace < Buffer.THRESHOLD) {
			return false;
		}else {
			return true;
		}
	}
	
	/**
	 * 将buffer区中的数据拷贝走，创建TempBuffer
	 * @return TempBuffer
	 */
	private TempBuffer swapDataAndMarkFree(){
		int length = buffer.getLength();
		ByteBuffer byteBuffer = ByteBuffer.allocate(length).order(ByteOrder.LITTLE_ENDIAN);
		long bufferStartAddress = segmentExcerpt.getPositonByOffset(buffer.getOffsetByLength(0));
		//写入从buffer copy的数据
		byteBuffer.put(new MemoryTool().getBytes(bufferStartAddress, length));
		byteBuffer.flip();
		//释放buffer的空间
		buffer.freeBuffer();
		return new TempBuffer(byteBuffer,segmentExcerpt);
	}
	
	private boolean unlock(){
		// TODO
		return false;
	}
	
	/**
	 * 检测buffer区是否需要进行merge，是则进行merge
	 * @param length 新增数据的长度，用于进行检测
	 * @return 
	 */
	private void checkMerge(int length){
		boolean check = checkFreeSpace(length);
		//检索结果为false，进行merge
		if (!check) {
			TempBuffer tempBuffer = swapDataAndMarkFree();
			MergeTaskQueue.putTask(tempBuffer);
		}
	}
}
