package com.cffex.nogc.memory.buffer;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

import sun.nio.ch.DirectBuffer;

import com.cffex.nogc.memory.NoGcByteBuffer;
import com.cffex.nogc.memory.SegmentExcerpt;
import com.cffex.nogc.memory.buffer.merge.MergeTask;
import com.cffex.nogc.memory.buffer.merge.MergeTaskSender;
import com.cffex.nogc.memory.buffer.merge.TempBuffer;
import com.cffex.nogc.memory.utils.MemoryTool;

/**
 * @author sunke
 * @ClassName BufferExcerpt
 * @Description: buffer操作接口的真实实现类， 包含了具体的底层操作原语
 */
public class BufferExcerpt extends AbstractBufferExcerpt{
	
	/**
	 * 原子boolean类型，用来实现spinlock
	 */
	private AtomicBoolean lock;
	
	/**
	 * spin lock的time_out时间，尝试10次
	 */
	private final int SPIN_LOCK_TIME_OUT = 10;
	
	/**
	 * spin lock尝试时的sleep时间
	 */
	private final int SPIN_LOCK_SLEEP_TIME = 20;
	
	public BufferExcerpt(SegmentExcerpt segmentExcerpt,NoGcByteBuffer noGcByteBuffer){
		super(segmentExcerpt, noGcByteBuffer);
		lock = new AtomicBoolean(false);
	}
	
	
	/**
	 * 更新buffer的长度
	 * @param length buffer长度的增量
	 * @return 长度增加后，可用长度相对于buffer的偏移量
	 */
	@Override
	protected int updateLength(int length){
		return buffer.updateLengthWithIncrement(length);
	}
	
	@Override
	protected void append(BufferLog log,int startLength){
		ByteBuffer byteBuffer = log.toBytebuffer();
		buffer.writeBytes(byteBuffer.array(), startLength);
	}
	
	@Override
	protected boolean lock(){
		int time = 0;
		while (!lock.compareAndSet(false, true) && time<SPIN_LOCK_TIME_OUT){
			try {
				Thread.sleep(SPIN_LOCK_SLEEP_TIME);	
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			time++;
        }
		if (time<SPIN_LOCK_TIME_OUT && lock.get()== true) {
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * 检测新增length长度的数据后，是否超界限
	 * @param length 新增length长度的数据
	 * @return true-->OK false --> need merge
	 */
	@Override
	protected boolean checkFreeSpace(int length){
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
	@Override
	protected TempBuffer swapDataAndMarkFree(){
		int length = buffer.getLength();
		DirectBuffer directBuffer = MemoryTool.allocate(length);
		NoGcByteBuffer temp = buffer.getNoGcByteBuffer();
		temp.flip();
		byte[] data = temp.getBytes(length);
		//写入从buffer copy的数据
		NoGcByteBuffer newBuffer = new NoGcByteBuffer(0, length, directBuffer);
		newBuffer.putBytes(data);
		newBuffer.flip();
		//释放buffer的空间
		buffer.freeBuffer();
		return new TempBuffer(newBuffer);
	}
	
	@Override
	protected void unlock(){
		lock.compareAndSet(true, false);
	}
	
	/**
	 * 检测buffer区是否需要进行merge，是则进行merge
	 * @param length 新增数据的长度，用于进行检测
	 * @return 
	 */
	@Override
	protected void checkMerge(int length){
		boolean check = checkFreeSpace(length);
		//检索结果为false，进行merge
		if (!check) {
			TempBuffer tempBuffer = swapDataAndMarkFree();
			MergeTaskSender.putTask(new MergeTask(tempBuffer, segmentExcerpt));
		}
	}
}
