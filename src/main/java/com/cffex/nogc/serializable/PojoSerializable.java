/**
 * @time 2014年8月26日
 * @author sunke
 */
package com.cffex.nogc.serializable;

import java.nio.ByteBuffer;


/**
 * @author sunke
 * @ClassName PojoSerializable
 * @Description: Pojo的序列化Serializable接口 
 */
public interface PojoSerializable {
	/**
	 * 将二进制数据读取并反序列化为object
	 * @param value 二进制数据
	 * @return object
	 */
	public Object readBinaryToObject(ByteBuffer value,Class<?> clazz);
	
	
	/**
	 * 将二进制数据读取并反序列化为object
	 * @param value 二进制数据
	 * @return object
	 */
	public Object readBinaryToObject(byte[] value,Class<?> clazz);
	
	
	
	/**
	 * 从value中获取class类型的第index个属性
	 * @param value 二进制数据
	 * @return object
	 */
	public Object getPropertyFromBinary(ByteBuffer value,Class<?> clazz,int index);
	
	/**
	 * 从value中获取class类型的第index个属性
	 * @param value 二进制数据
	 * @return object
	 */
	public Object getPropertyFromBinary(byte[] value,Class<?> clazz,int index);
	
	
	/**
	 * 从value中获取class类型的第index个属性的二进制数据
	 * @param value 二进制数据
	 * @return object
	 */
	public byte[] getPropertyBinaryFromBinary(ByteBuffer value,Class<?> clazz,int index);
	
	/**
	 * 从value中获取class类型的第index个属性的二进制数据
	 * @param value 二进制数据
	 * @return object
	 */
	public byte[] getPropertyBinaryFromBinary(byte[] value,Class<?> clazz,int index);
	
	/**
	 * 从value中获取class类型的第index个属性的RawValue
	 * @param value 二进制数据
	 * @return object
	 */
	public byte[] getPropertyRawValueFromBinary(ByteBuffer value,Class<?> clazz,int index);
	
	/**
	 * 从value中获取class类型的第index个属性的RawValue
	 * @param value 二进制数据
	 * @return object
	 */
	public byte[] getPropertyRawValueFromBinary(byte[] value,Class<?> clazz,int index);
	
	/**
	 * 将object序列化为cson数据
	 * @param value object
	 * @return 序列化后的ByteBuffer-->flip:postion set zero.
	 */
	public ByteBuffer writeObjectToByteBuffer(Object value);
}
