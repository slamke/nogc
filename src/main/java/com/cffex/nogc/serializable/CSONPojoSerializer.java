/**
 * @time 2014年8月26日
 * @author sunke
 */
package com.cffex.nogc.serializable;

import java.nio.ByteBuffer;

import com.cffex.nogc.cson.core.utils.CSONSerializerTool;

/**
 * @author sunke
 * @ClassName CSONPojoSerializer
 * @Description: 基于cson进行序列化的序列化器 
 */
public class CSONPojoSerializer implements PojoSerializable {

	/* (non-Javadoc)
	 * @see com.cffex.nogc.serializable.PojoSerializable#readBinaryToObject(com.sun.corba.se.impl.ior.ByteBuffer)
	 */
	@Override
	public Object readBinaryToObject(ByteBuffer value,Class<?> clazz) {
		// TODO Auto-generated method stub
		return CSONSerializerTool.readCSONToObject(value, clazz);
	}

	/* (non-Javadoc)
	 * @see com.cffex.nogc.serializable.PojoSerializable#writeObjectToByteBuffer(java.lang.Object)
	 */
	@Override
	public ByteBuffer writeObjectToByteBuffer(Object value) {
		// TODO Auto-generated method stub
		return CSONSerializerTool.serializeObjectToCSON(value, value.getClass());
	}

}
