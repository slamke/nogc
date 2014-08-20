package com.cffex.nogc.memory.buffer;

import java.util.List;

/**
 * 
 * @author sunke
 * @ClassName BufferOperatable
 * @Description: Buffer区操作接口
 */
public interface BufferOperatable {
	
	/**
	 * 向buffer区添加操作
	 * @param log 操作信息
	 * @return
	 */
	public boolean appendOperation(BufferLog log);

	/**
	 * 准备向buffer区追加操作长度为length的数据，尝试加锁
	 * @param length 数据的长度
	 * @return 加锁是否成功
	 */
	public boolean tryLockWithLength(int length);

	/**
	 * 从buffer区获取id对应的所有bufferlog
	 * @param id 标志id
	 * @return log列表
	 */
	public List<BufferLog> getById(long id);

	/**
	 * 从buffer区获取id对应item的第index个属性的数据
	 * @param id item的id
	 * @param index 属性的索引
	 * @return property的数据
	 */
	public byte[] getPropertyById(long id, int index);
}
