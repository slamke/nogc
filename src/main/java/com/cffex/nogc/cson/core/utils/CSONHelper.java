/**
 * @time 2014年8月26日
 * @author sunke
 */
package com.cffex.nogc.cson.core.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.cffex.nogc.cson.access.EntitySchemaCache;
import com.cffex.nogc.cson.access.EntitySerializerCache;
import com.cffex.nogc.cson.access.GeneralEntityToCSON;
import com.cffex.nogc.cson.core.CSONDocument;
import com.cffex.nogc.cson.core.entityInterface.access.IEntityRandomAccess;
import com.cffex.nogc.memory.NoGcByteBuffer;

/**
 * @author sunke
 * @ClassName CSONSerializerTool
 * @Description: cson序列化的工具类，所有的序列化器使用 EntitySerializerCache进行缓存
 */
public class CSONHelper {
	private CSONHelper() {
	}

	/**
	 * 将object使用cson进行序列化，结果保存在比亚特buffer中
	 * @param object 待序列化的object 
	 * @param clazz object的clazz类型
	 * @return 序列化的结果-->flip:postion set zero.
	 */
	public static ByteBuffer serializeObjectToCSON(Object object, Class<?> clazz) {
		GeneralEntityToCSON serializer = EntitySerializerCache
				.getEntityToCSON(clazz);
		ByteBuffer outBuffer = ByteBuffer.wrap(new byte[4096]).order(
				ByteOrder.LITTLE_ENDIAN);
		Tuple<IEntityRandomAccess, ByteBuffer> buffer = serializer
				.writeObjectToCSON(object, outBuffer);
		buffer.second.flip();
		return buffer.second;
	}

	/**
	 * 将cson的二进制数据，反序列化为object
	 * @param buffer 二进制数据
	 * @param clazz object的class信息
	 * @return 反序列化后的object
	 */
	public static Object readCSONToObject(ByteBuffer buffer, Class<?> clazz) {
		GeneralEntityToCSON serializer = EntitySerializerCache
				.getEntityToCSON(clazz);
		Object object = serializer.readCSONToObject(buffer);
		return object;
	}
	
	/**
	 * 获取cson结构的第index个属性的值
	 * @param byteBuffer
	 * @param index
	 * @param clazz
	 * @return 属性的值
	 */
	public static Object getPropertyByIndex(ByteBuffer byteBuffer, int index,Class<?> clazz) {
		CSONDocument document = new CSONDocument(EntitySchemaCache.objectSchemaFactory(clazz), byteBuffer);
		return document.getValue(index);
	}
	
	/**获取cson结构的第index个属性的rawValue
	 * @param byteBuffer
	 * @param index
	 * @param clazz
	 * @return
	 */
	public static byte[] getPropertyRawValueByIndex(ByteBuffer byteBuffer, int index,Class<?> clazz) {
		CSONDocument document = new CSONDocument(EntitySchemaCache.objectSchemaFactory(clazz), byteBuffer);
		return document.getRawValue(index);
	}

	/**获取cson结构的第index个属性的Binary(无typecode)
	 * @param byteBuffer
	 * @param index
	 * @param clazz
	 * @return
	 */
	public byte[] getPropertyBinaryByIndex(ByteBuffer byteBuffer, int index,Class<?> clazz) {
		CSONDocument document = new CSONDocument(EntitySchemaCache.objectSchemaFactory(clazz), byteBuffer);
		return document.getRawValue(index,false);
	}
	/**
	 * 从noGcByteBuffer中读取一个cson，从当前位置开始
	 * @param noGcByteBuffer 带读取的noGcByteBuffer
	 * @return bytebuffer-->已经flip
	 */
	public static ByteBuffer getCSONFromNoGcByteBuffer(NoGcByteBuffer noGcByteBuffer){
		int len = noGcByteBuffer.getInt();
		ByteBuffer buffer = ByteBuffer.allocate(len+5).order(ByteOrder.LITTLE_ENDIAN);
		buffer.putInt(len);
		buffer.put(noGcByteBuffer.getBytes(len));
		buffer.flip();
		return buffer;
	}
	
	/**
	 * 从ByteBuffer中读取一个cson，从当前位置开始
	 * @param byteBuffer 待读取的byteBuffer
	 * @return byte[]
	 */
	public static byte[] getCSONFromByteBuffer(ByteBuffer byteBuffer){
		int len = byteBuffer.getInt(byteBuffer.position());
		byte[] value = new byte[len+4];
		byteBuffer.get(value);
		return value;
	}
	
}
