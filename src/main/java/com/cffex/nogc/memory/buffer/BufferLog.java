package com.cffex.nogc.memory.buffer;

import java.nio.ByteBuffer;

import com.cffex.nogc.cson.core.utils.CSONHelper;
import com.cffex.nogc.memory.NoGcByteBuffer;
import com.cffex.nogc.serializable.PojoSerializable;
import com.cffex.nogc.serializable.PojoSerializerFactory;

/**
 * @author sunke TaoZhou
 * @ClassName BufferLog
 * @Description: buffer区中记录的log -->使用cson序列化后，保存在buffer区中
 */
public class BufferLog {
	
	/**
	 * buffer操作的四种类型
	 * @author sunke
	 * @ClassName BufferLogType
	 * @Description: TODO
	 */
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
				BufferLog log = null;
				do {
					ByteBuffer buffer = CSONHelper.getCSONFromNoGcByteBuffer(cursor);
					log = getBufferLogFromCSON(buffer);
				} while (log.getId() != id && hasNext());
				if (log != null && log != null && log.getId() == id) {
					return log;
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
				BufferLog log = null;
				do {
					ByteBuffer buffer = CSONHelper.getCSONFromNoGcByteBuffer(cursor);
					log = getBufferLogFromCSON(buffer);
					//短路与,每个log，只读flag和id
				} while ((log.getId() != id || log.getFlag() != BufferLogType.UPDATE_PROPERTY ||
						log.getIndex() != propertyIndex)&& hasNext());
				if (log != null && id == log.getId() && log.getFlag() == BufferLogType.UPDATE_PROPERTY &&
						log.getIndex() == propertyIndex) {
					return log;
				}
				return null;
			}else {
				return null;
			}
		}
		/**
		 * 获取next log记录
		 * @return next log记录
		 */
		public BufferLog next(){
			if (hasNext()) {
				ByteBuffer buffer = CSONHelper.getCSONFromNoGcByteBuffer(cursor);
				BufferLog log = getBufferLogFromCSON(buffer);
				return log;
			}
			return null;
		}
		
		private BufferLog getBufferLogFromCSON(ByteBuffer byteBuffer) {
			PojoSerializable tool = PojoSerializerFactory.getSerializer();
			BufferLog log = (BufferLog)tool.readBinaryToObject(byteBuffer, BufferLog.class);
			return log;
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
	 * 更新属性：data-->rawValue
	 */
	private byte[] value;
	/**
	 * 更新属性时，属性的key
	 */
	private String schemaKey;
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
		this.schemaKey = null;
	}

	/**构造一个新的buffer log
	 * @param flag log类型
	 * @param id log的id
	 * @param value 值
	 * @param index 属性的index
	 */
	public BufferLog(BufferLogType flag, long id, byte[] value, int index,String schemaKey) {
		super();
		this.flag = flag;
		this.id = id;
		this.value = value;
		this.index = index;
		this.schemaKey = schemaKey;
	}
	
	
	
	/**
	 * 无参构造函数-->使用cson序列化的必要条件
	 */
	public BufferLog() {
		super();
	}

	public int getIndex(){
		return this.index;
	}
	
	public byte[] getValue() {
		return value;
	}

	public byte[] getValue(int index) {
		// TODO Auto-generated method stub
		try {
			return CSONHelper.getPropertyRawValueByIndex(toBytebuffer(), index, Class.forName(getSchemaKey()));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public long getId() {
		return id;
	}

	public BufferLogType getFlag() {
		return flag;
	}
	
	public String getSchemaKey() {
		return schemaKey;
	}
	
	

	public void setFlag(BufferLogType flag) {
		this.flag = flag;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setValue(byte[] value) {
		this.value = value;
	}

	public void setSchemaKey(String schemaKey) {
		this.schemaKey = schemaKey;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * 将bufferLog转为bytebuffer用于存储
	 * @return bytebuffer-->flip:postion to zero,length-->limit
	 */
	public ByteBuffer toBytebuffer(){
		PojoSerializable tool = PojoSerializerFactory.getSerializer();
		ByteBuffer byteBuffer = tool.writeObjectToByteBuffer(this);
		return byteBuffer;
	}


}
