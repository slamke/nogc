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
	 * 将object序列化为cson数据
	 * @param value object
	 * @return 序列化后的ByteBuffer-->flip:postion set zero.
	 */
	public ByteBuffer writeObjectToByteBuffer(Object value);
}
