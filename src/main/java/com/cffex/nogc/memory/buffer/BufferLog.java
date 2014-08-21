package com.cffex.nogc.memory.buffer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.cffex.nogc.memory.NoGcByteBuffer;
import com.cffex.nogc.memory.buffer.exception.BufferLogException;

/**
 * @author sunke
 * @ClassName BufferLog
 * @Description: buffer区中记录的log 
 */
public class BufferLog {
	
	public BufferLogType getFlag() {
		return flag;
	}
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
	
	public static class BufferLogCusor{
		private NoGcByteBuffer cursor;
		public BufferLogCusor(NoGcByteBuffer noGcByteBuffer){
			this.cursor = noGcByteBuffer;
		}
		public boolean hasNext(){
			return cursor.hasRemaining();
		}
		/**
		 * 获取对应id的next log记录
		 * @param id
		 * @return 对应id的next log记录
		 */
		public BufferLog next(long id){
			if (hasNext()) {
				byte flag = cursor.get();
				BufferLogType type = BufferLogType.getBufferLogType(flag);
				long logId = cursor.getLong();
				while (id != logId && hasNext()) {
					try {
						int dataLength = getLength(type);
						cursor.position(cursor.position()+dataLength);
						continue;
					} catch (Exception e) {
						e.printStackTrace();
						break;
					}
				}
				if (id == logId && hasNext()) {
					try {
						return getCurrentLog(logId,type);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				return null;
			}else {
				return null;
			}
		}
		
		/**
		 * 获取对应id和propertyIndex的next log记录
		 * @param id
		 * @param propertyIndex
		 * @return 对应id的next log记录
		 */
		public BufferLog next(long id,int propertyIndex){
			if (hasNext()) {
				byte flag = cursor.get();
				BufferLogType type = BufferLogType.getBufferLogType(flag);
				long logId = cursor.getLong();
				try {
					//短路与,每个log，只读flag和id
					while ((id != logId || type != BufferLogType.UPDATE_PROPERTY || 
							cursor.getInt(cursor.position()) != propertyIndex) &&  hasNext()) {
						try {
							int dataLength = getLength(type);
							cursor.position(cursor.position()+dataLength);
							continue;
						} catch (Exception e) {
							e.printStackTrace();
							break;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (id == logId && type == BufferLogType.UPDATE_PROPERTY &&
						cursor.getInt(cursor.position()) == propertyIndex) {
					try {
						return getCurrentLog(logId,type);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				return null;
			}else {
				return null;
			}
		}
		private BufferLog getCurrentLog(long id,BufferLogType type) throws Exception{
			BufferLog log = null;
			switch(type){
				case INSERT:
				case UPDATE_ALL:
					{
						int dataLength = cursor.getInt();
						byte[] data = cursor.getBytes(dataLength);
						log = new BufferLog(type, id, data);
						return log;
					}
				case UPDATE_PROPERTY:
					{
						int index = cursor.getInt();
						int dataLength = cursor.getInt();
						byte[] data = cursor.getBytes(dataLength);
						log =  new BufferLog(type, id, data, index);
						return log;
					}
				case DELETE:
					return new BufferLog(type, id, null);
				default:
					throw new Exception("Buffer Type error");
			}
		}
		/**
		 * 获取next log记录
		 * @return next log记录
		 */
		public BufferLog next(){
			if (hasNext()) {
				try {
					byte flag = cursor.get();
					BufferLogType type = BufferLogType.getBufferLogType(flag);
					long logId = cursor.getLong();
					return getCurrentLog(logId,type);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		private int getLength(BufferLogType type) throws Exception{
			if (type == BufferLogType.UPDATE_PROPERTY) {
				cursor.getInt();//index
				return cursor.getInt();
			}else if (type == BufferLogType.INSERT ||type == BufferLogType.UPDATE_ALL) {
				return cursor.getInt();
			}else {
				return 0;
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
	
	
	public byte[] getValue() {
		return value;
	}

	/**
	 * 计算本log的长度
	 * @return 长度值
	 * @throws BufferLogException
	 */
	public int getLength() throws BufferLogException{
		//insert  UPDATE_ALL:  flag(1)+id(8)+length(4)+data(X)
		if (flag == BufferLogType.INSERT || flag == BufferLogType.UPDATE_ALL) {
			if (value == null) {
				throw new BufferLogException("Value cannot be null when insert or update whole item.");
			}else {
				return 1+8+4+value.length;
			}
		}
		//DELETE:flag+id
		else if(flag == BufferLogType.DELETE){
			return 1+8;
		}
		//UPDATE_PROPERTY: flag(1)+id(8)+index(4)+length(4)+data(X)
		else if (flag == BufferLogType.UPDATE_PROPERTY) {
			if (value == null) {
				throw new BufferLogException("Value cannot be null when update item property.");
			}else {
				return 1+8+4+4+value.length;
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
			//insert  UPDATE_ALL:  flag(1)+id(8)+length(4)+data(X)
			if (flag == BufferLogType.INSERT || flag == BufferLogType.UPDATE_ALL) {
				buffer.putInt(value.length);
				buffer.put(value);
			}//UPDATE_PROPERTY: flag(1)+id(8)+index(4)+length(4)+data(X)
			else if(flag == BufferLogType.UPDATE_PROPERTY) {
				if (index <0) {
					throw new IllegalArgumentException("Index cannot be negative when update a property of an object");
				}else {
					buffer.putInt(index);
					buffer.putInt(value.length);
					buffer.put(value);
				}
			}
			buffer.flip();
			return buffer;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (BufferLogException e) {
			e.printStackTrace();
		}
		return null;
	}
 
}
