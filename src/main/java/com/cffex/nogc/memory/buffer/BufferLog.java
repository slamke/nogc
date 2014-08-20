package com.cffex.nogc.memory.buffer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.cffex.nogc.memory.buffer.exception.BufferLogException;

/**
 * @author sunke
 * @ClassName BufferLog
 * @Description: buffer区中记录的log 
 */
public class BufferLog {
	
	public static enum BufferLogType{
		INSERT(0),
		DELETE(1),
		UPDATE_ALL(2),
		UPDATE_PROPERTY(3);
		
		private final byte value;
		public static BufferLogType[] array = new BufferLogType[]{INSERT,DELETE,UPDATE_ALL,UPDATE_PROPERTY};
		
		public byte getValue(){
			return value;
		}
		private BufferLogType(int value){
			this.value = (byte)value;
		}
		
		public static BufferLogType getBufferLogType(byte value){
			return array[value];
		}
	}
	
	/**
	 * log的类型：插入，删除，更新全部，更新属性
	 */
	private BufferLogType flag;
	/**
	 * log对应记录的id
	 */
	private long id;
	/**
	 * log的数据内容
	 * 插入：item的整体数据
	 * 删除：null
	 * 更新全部：item的整体数据
	 * 更新属性：bytecode+data
	 */
	private byte[] value;
	/**
	 * 更新属性时，属性的index索引
	 */
	private int index;
	
	/**	构造一个新的buffer log
	 * @param flag log类型
	 * @param id   log的id
	 * @param value 值
	 */
	public BufferLog(BufferLogType flag, long id, byte[] value) {
		super();
		this.flag = flag;
		this.id = id;
		this.value = value;
		this.index = -1;
	}

	/**构造一个新的buffer log
	 * @param flag log类型
	 * @param id log的id
	 * @param value 值
	 * @param index 属性的index
	 */
	public BufferLog(BufferLogType flag, long id, byte[] value, int index) {
		super();
		this.flag = flag;
		this.id = id;
		this.value = value;
		this.index = index;
	}
	
	/**
	 * 计算本log的长度
	 * @return 长度值
	 * @throws BufferLogException
	 */
	public int getLength() throws BufferLogException{
		//insert  UPDATE_ALL:flag+id+data
		if (flag == BufferLogType.INSERT || flag == BufferLogType.UPDATE_ALL) {
			if (value == null) {
				throw new BufferLogException("Value cannot be null when insert or update whole item.");
			}else {
				return 1+8+value.length;
			}
		}
		//DELETE:flag+id
		else if(flag == BufferLogType.DELETE){
			return 1+8;
		}
		//UPDATE_PROPERTY: flag+id+index+data
		else if (flag == BufferLogType.UPDATE_PROPERTY) {
			if (value == null) {
				throw new BufferLogException("Value cannot be null when update item property.");
			}else {
				return 1+8+4+value.length;
			}
		}
		return 0;
	}
	/**
	 * 将bufferLog转为bytebuffer用于写入
	 * @return bytebuffer
	 */
	public ByteBuffer toBytebuffer(){
		try {
			int length = getLength();
			ByteBuffer buffer = ByteBuffer.allocate(length).order(ByteOrder.LITTLE_ENDIAN);
			buffer.put(flag.getValue());
			buffer.putLong(id);
			//insert:flag+id+data
			if (flag == BufferLogType.INSERT || flag == BufferLogType.UPDATE_ALL) {
				buffer.put(value);
			}else if(flag == BufferLogType.UPDATE_PROPERTY) {
				if (index <0) {
					throw new IllegalArgumentException("Index cannot be negative when update a property of an object");
				}else {
					buffer.putInt(index);
					buffer.put(value);
				}
			}
			return buffer;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (BufferLogException e) {
			e.printStackTrace();
		}
		return null;
	}

	//public static BufferLog constructBufferLog(byte []){
	//	return null;
	//}  
}
