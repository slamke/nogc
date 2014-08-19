package com.cffex.nogc.memory.buffer;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author sunke
 * @ClassName Buffer
 * @Description: segment中的buffer区
 */
public class Buffer {
	/**
	 * buffer区相对于segment的offset
	 */
	public static final int OFFSET = 0;

	/**
	 * buffer区的剩余空闲空间的阈值，低于这个空间时，需要进行merge 暂设为256Byte
	 */
	public static final int THRESHOLD = 256;
	/**
	 * buffer区的容量大小，暂设为：128K
	 */
	public static final int CAPACITY = 128 * 1024;

	// private transient int state;
	/**
	 * 使用原子性的integer，用以支持spin lock
	 */
	private volatile AtomicInteger length;
	
	/**
	 * 增加buffer区的长度，增量为increment
	 * @param increment 长度增量
	 * @return 长度增加后，可以使用的buffer区空间的起始点
	 */
	public int updateLengthWithIncrement(int increment) {
		int v = 0;
        do {
            v = length.get();
        }while (!length.compareAndSet(v, v + increment));
        return v;
	}

	// public int getState() {
	// return state;
	// }
	// public void setState(int state) {
	// this.state = state;
	// }

	public int getOffsetByLength(int length) {
		return OFFSET+length;
	}
}
