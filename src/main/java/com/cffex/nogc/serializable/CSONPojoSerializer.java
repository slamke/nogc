/**
 * @time 2014年8月26日
 * @author sunke
 */
package com.cffex.nogc.serializable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.cffex.nogc.cson.core.utils.CSONHelper;

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
		return CSONHelper.readCSONToObject(value, clazz);
	}

	/* (non-Javadoc)
	 * @see com.cffex.nogc.serializable.PojoSerializable#writeObjectToByteBuffer(java.lang.Object)
	 */
	@Override
	public ByteBuffer writeObjectToByteBuffer(Object value) {
		return CSONHelper.serializeObjectToCSON(value, value.getClass());
	}

	/* (non-Javadoc)
	 * @see com.cffex.nogc.serializable.PojoSerializable#readBinaryToObject(byte[], java.lang.Class)
	 */
	@Override
	public Object readBinaryToObject(byte[] value, Class<?> clazz) {
		return CSONHelper.readCSONToObject(ByteBuffer.wrap(value).order(ByteOrder.LITTLE_ENDIAN), clazz);
	}

	/* (non-Javadoc)
	 * @see com.cffex.nogc.serializable.PojoSerializable#getPropertyFromBinary(java.nio.ByteBuffer, java.lang.Class, int)
	 */
	@Override
	public Object getPropertyFromBinary(ByteBuffer value, Class<?> clazz,
			int index) {
		// TODO Auto-generated method stub
		return CSONHelper.getPropertyByIndex(value, index, clazz);
	}

	/* (non-Javadoc)
	 * @see com.cffex.nogc.serializable.PojoSerializable#getPropertyFromBinary(byte[], java.lang.Class, int)
	 */
	@Override
	public Object getPropertyFromBinary(byte[] value, Class<?> clazz, int index) {
		// TODO Auto-generated method stub
		return CSONHelper.getPropertyByIndex(ByteBuffer.wrap(value).order(ByteOrder.LITTLE_ENDIAN), index, clazz);
	}

	/* (non-Javadoc)
	 * @see com.cffex.nogc.serializable.PojoSerializable#getPropertyBinaryFromBinary(java.nio.ByteBuffer, java.lang.Class, int)
	 */
	@Override
	public byte[] getPropertyBinaryFromBinary(ByteBuffer value, Class<?> clazz,
			int index) {
		return CSONHelper.getPropertyBinaryByIndex(value,index,clazz);
	}

	/* (non-Javadoc)
	 * @see com.cffex.nogc.serializable.PojoSerializable#getPropertyBinaryFromBinary(byte[], java.lang.Class, int)
	 */
	@Override
	public byte[] getPropertyBinaryFromBinary(byte[] value, Class<?> clazz,
			int index) {
		// TODO Auto-generated method stub
		return CSONHelper.getPropertyBinaryByIndex(ByteBuffer.wrap(value).order(ByteOrder.LITTLE_ENDIAN),index,clazz);
	}

	/* (non-Javadoc)
	 * @see com.cffex.nogc.serializable.PojoSerializable#getPropertyRawValueFromBinary(java.nio.ByteBuffer, java.lang.Class, int)
	 */
	@Override
	public byte[] getPropertyRawValueFromBinary(ByteBuffer value,
			Class<?> clazz, int index) {
		// TODO Auto-generated method stub
		return CSONHelper.getPropertyRawValueByIndex(value, index, clazz);
	}

	/* (non-Javadoc)
	 * @see com.cffex.nogc.serializable.PojoSerializable#getPropertyRawValueFromBinary(byte[], java.lang.Class, int)
	 */
	@Override
	public byte[] getPropertyRawValueFromBinary(byte[] value, Class<?> clazz,
			int index) {
		// TODO Auto-generated method stub
		return CSONHelper.getPropertyRawValueByIndex(ByteBuffer.wrap(value).order(ByteOrder.LITTLE_ENDIAN), index, clazz);
	}

}
