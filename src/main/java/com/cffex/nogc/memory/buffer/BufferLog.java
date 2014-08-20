package com.cffex.nogc.memory.buffer;

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
		public byte getValue(){
			return value;
		}
		private BufferLogType(int value){
			this.value = (byte)value;
		}
		
		public static BufferLogType getBufferLogType(byte value){
			switch (value) {
			case 0:
				return INSERT;
			case 1:
				return DELETE;
			case 2:
				return UPDATE_ALL;
			case 3:
				return UPDATE_PROPERTY;
			default:
				return INSERT;
			}
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
		if (flag == BufferLogType.UPDATE_PROPERTY) {
			throw new IllegalArgumentException("Can not use this constructor when update a prperty "
					+ "of an object.Use public BufferLog(int flag, long id, byte[] value,int index) to set index.");
		}
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
		if (flag == BufferLogType.UPDATE_PROPERTY &&
				index < 0) {
			throw new IllegalArgumentException("Index cannot be negative when update a property of an object");
		}
		this.flag = flag;
		this.id = id;
		this.value = value;
		this.index = index;
	}
	
	public byte[] getdata(){
		return null;
	} 

}
