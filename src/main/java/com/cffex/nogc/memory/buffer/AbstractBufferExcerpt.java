/**
 * @time 2014年8月21日
 * @author sunke
 */
package com.cffex.nogc.memory.buffer;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.cffex.nogc.memory.NoGcByteBuffer;
import com.cffex.nogc.memory.SegmentExcerpt;
import com.cffex.nogc.memory.buffer.BufferLog.BufferLogCusor;
import com.cffex.nogc.memory.buffer.BufferLog.BufferLogType;
import com.cffex.nogc.memory.buffer.merge.TempBuffer;
import com.cffex.nogc.serializable.PojoSerializable;
import com.cffex.nogc.serializable.PojoSerializerFactory;
import com.cffex.nogc.serializable.PojoSerializerFactory.PojoSerializerType;

/**
 * @author sunke
 * @ClassName AbstractBufferExcerpt
 * @Description: 实现BufferOperatable接口的抽象类，只实现了顶层的BufferOperatable的方法
 */
public abstract class AbstractBufferExcerpt implements BufferOperatable {

	protected Buffer buffer;
	protected SegmentExcerpt segmentExcerpt;

	public AbstractBufferExcerpt(SegmentExcerpt segmentExcerpt,
			NoGcByteBuffer noGcByteBuffer) {
		this.buffer = new Buffer(noGcByteBuffer);
		this.segmentExcerpt = segmentExcerpt;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cffex.nogc.memory.buffer.BufferOperatable#appendOperation(com.cffex
	 * .nogc.memory.buffer.BufferLog)
	 */
	@Override
	public final boolean appendOperation(BufferLog log) {
		try {
			ByteBuffer value = log.toBytebuffer();
			int length = value.limit();
			int startPoint = updateLength(length);
			// 当有update时，segment状态变为非只读
			if (log.getFlag() == BufferLogType.UPDATE_PROPERTY
					|| log.getFlag() == BufferLogType.UPDATE_ALL) {
				segmentExcerpt.setReadonly(false);
			}
			append(value, startPoint);
			return true;
		} finally {
			unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cffex.nogc.memory.buffer.BufferOperatable#getById(long)
	 */
	@Override
	public final List<BufferLog> getById(long id) {
		// TODO Auto-generated method stub
		int modCount = buffer.getModCount();
		NoGcByteBuffer noGcByteBuffer = buffer.getNoGcByteBuffer();
		BufferLogCusor cusor = new BufferLogCusor(noGcByteBuffer);
		List<BufferLog> logs = new ArrayList<BufferLog>();
		while (cusor.hasNext()) {
			BufferLog log = cusor.next(id);
			if (log != null) {
				logs.add(log);
			} else {
				break;
			}
		}
		if (modCount == buffer.getModCount()) {
			List<BufferLog> res = new ArrayList<BufferLog>();
			if (logs != null) {
				for (int i = logs.size()-1; i > -1 ; i--) {
					BufferLog log = logs.get(i);
					//已拿到最新数据
					if (log.getFlag() == BufferLogType.UPDATE_ALL || log.getFlag() == BufferLogType.INSERT
							||log.getFlag() == BufferLogType.DELETE) {
						res.add(log);
						break;
					}
					//部分数据
					else {
						res.add(log);
					}
				}
			}
			return res;
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cffex.nogc.memory.buffer.BufferOperatable#getPropertyById(long,
	 * int)
	 */
	@Override
	public final byte[] getPropertyById(long id, int index,Class<?> clazz) {
		// TODO Auto-generated method stub
		int modCount = buffer.getModCount();
		NoGcByteBuffer noGcByteBuffer = buffer.getNoGcByteBuffer();
		BufferLogCusor cusor = new BufferLogCusor(noGcByteBuffer);
		List<BufferLog> logs = new ArrayList<BufferLog>();
		while (cusor.hasNext()) {
			BufferLog log = cusor.next(id);
			if (log != null) {
				logs.add(log);
			} else {
				break;
			}
		}
		// 最后一条记录是最新的记录。

		if (modCount == buffer.getModCount() && logs.size() > 0) {
			for (int i = logs.size() -1;i > -1;i--) {
				BufferLog bufferLog = logs.get(i);
				if (bufferLog.getFlag() == BufferLogType.INSERT || bufferLog.getFlag() == BufferLogType.UPDATE_ALL) {
					PojoSerializable serializable = PojoSerializerFactory.getSerializerByType(PojoSerializerType.CSON);
					return serializable.getPropertyRawValueFromBinary(bufferLog.getValue(), clazz, index);
				}else if (bufferLog.getFlag() == BufferLogType.UPDATE_PROPERTY){
					if (bufferLog.getIndex() == index) {
						return bufferLog.getValue();
					}
				}
			}
		}
			return null;
	}

	/**
	 * 更新buffer的长度
	 * 
	 * @param length
	 *            buffer长度的增量
	 * @return 长度增加后，可用长度相对于buffer的偏移量
	 */
	abstract protected int updateLength(int length);

	abstract protected void append(ByteBuffer log, int startLength);

	abstract protected boolean lock();

	/**
	 * 检测新增length长度的数据后，是否超界限
	 * 
	 * @param length
	 *            新增length长度的数据
	 * @return true-->OK false --> need merge
	 */
	abstract protected boolean checkFreeSpace(int length);

	/**
	 * 将buffer区中的数据拷贝走，创建TempBuffer
	 * 
	 * @return TempBuffer
	 */
	abstract protected TempBuffer swapDataAndMarkFree();

	abstract protected void unlock();

	/**
	 * 检测buffer区是否需要进行merge，是则进行merge
	 * 
	 * @param length
	 *            新增数据的长度，用于进行检测
	 * @return
	 */
	abstract protected void checkMerge(int length);
}
