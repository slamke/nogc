package com.cffex.nogc.memory.buffer;

import com.cffex.nogc.memory.NoGcByteBuffer;
import com.cffex.nogc.memory.utils.PotentialProblem;

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

	/**
	 * buffer区merge的count计数，读buffer前后进行modcount一致性检测
	 */
	@PotentialProblem(reason="防止线程读buffer的过程中，buffer区被merge，其读取的数据被覆盖掉，导致dirty data",
			problem="")
	private transient int modCount;
	/**
	 * buffer区有效数据的长度
	 */
	private volatile int length;
	
	private NoGcByteBuffer noGcByteBuffer;
	
	/**
	 * 构造函数
	 */
	public Buffer(NoGcByteBuffer noGcByteBuffer) {
		super();
		this.length = 0;
		this.modCount = 0;
		this.noGcByteBuffer = noGcByteBuffer;
	}

	/**
	 * 增加buffer区的长度，增量为increment
	 * @param increment 长度增量
	 * @return 长度增加后，可以使用的buffer区空间的起始点
	 */
	public int updateLengthWithIncrement(int increment) {
		int v = length;
        length = length + v;
        return v;
	}
	
	/**
	 * 释放buffer区，将buffer区长度置为0即可
	 */
	public void freeBuffer(){
		length = 0;
		modCount++;
	}

	/**
	 * 获取modCount-->buffer的状态改变。。。
	 * @return modCount
	 */
	public int getModCount() {
		return modCount;
	}



	/*public void addModCount() {
		this.modCount++;
	}*/

	/*public int getOffsetByLength(int length) {
		return OFFSET+length;
	}*/

	public int getLength() {
		return length;
	}
	
	public void writeBytes(byte[] value,int offset){
		noGcByteBuffer.putBytes(offset, value);
	}
	
	public NoGcByteBuffer getNoGcByteBuffer(){
		return noGcByteBuffer.duplicate();
	}
}
