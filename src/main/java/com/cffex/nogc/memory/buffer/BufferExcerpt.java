package com.cffex.nogc.memory.buffer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import com.cffex.nogc.memory.SegmentExcerpt;
import com.cffex.nogc.memory.buffer.BufferLog.BufferLogCusor;
import com.cffex.nogc.memory.buffer.BufferLog.BufferLogType;
import com.cffex.nogc.memory.buffer.exception.BufferLogException;
import com.cffex.nogc.memory.utils.MemoryTool;

/**
 * @author sunke
 * @ClassName BufferExcerpt
 * @Description: buffer操作接口的真实实现类， 包含了具体的底层操作原语
 */
public class BufferExcerpt implements BufferOperatable{
	
	private Buffer buffer;
	protected SegmentExcerpt segmentExcerpt;
	
	public BufferExcerpt(SegmentExcerpt segmentExcerpt){
		this.buffer = new Buffer();
		this.segmentExcerpt = segmentExcerpt;
	}
	
	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.buffer.BufferOperatable#appendOperation(com.cffex.nogc.memory.buffer.BufferLog)
	 */
	@Override
	public final boolean appendOperation(BufferLog log) {
		try {
			int length = log.getLength();
			int startPoint = updateLength(length);
			append(log,startPoint);
			return true;
		} catch (BufferLogException e) {
			e.printStackTrace();
			return false;
		}finally{
			unlock();
		}
	}
	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.buffer.BufferOperatable#tryLockWithLength(int)
	 */
	@Override
	public final boolean tryLockWithLength(int length) {
		boolean lockResult = lock();
		if (lockResult) {
			checkMerge(length);
		}
		return lockResult;
	}
	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.buffer.BufferOperatable#getById(long)
	 */
	@Override
	public final List<BufferLog> getById(long id) {
		// TODO Auto-generated method stub
		int length = buffer.getLength();
		int offset = buffer.getOffsetByLength(0);
		long position = segmentExcerpt.getPositonByOffset(offset);
		BufferLogCusor cusor = new BufferLogCusor(position, length);
		List<BufferLog> logs = new ArrayList<BufferLog>();	
		while (cusor.hasNext()) {
			BufferLog log = cusor.next(id);
			if (log != null) {
				logs.add(log);
			}else {
				break;
			}
		}
		return logs;
	}
	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.buffer.BufferOperatable#getPropertyById(long, int)
	 */
	@Override
	public final byte[] getPropertyById(long id, int index) {
		// TODO Auto-generated method stub
		int length = buffer.getLength();
		int offset = buffer.getOffsetByLength(0);
		long position = segmentExcerpt.getPositonByOffset(offset);
		BufferLogCusor cusor = new BufferLogCusor(position, length);
		List<BufferLog> logs = new ArrayList<BufferLog>();	
		while (cusor.hasNext()) {
			BufferLog log = cusor.next(id,index);
			if (log != null) {
				logs.add(log);
			}else {
				break;
			}
		}
		//最后一条记录是最新的记录。
		if (logs.size() >0) {
			return logs.get(logs.size() -1).getValue();
		}else {
			return null;
		}
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
		int offset = buffer.getOffsetByLength(startLength);
		long position = segmentExcerpt.getPositonByOffset(offset);
		MemoryTool tool = new MemoryTool();
		tool.writeBytes(byteBuffer.array(), position);
	}
	private boolean lock(){
		// TODO
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
