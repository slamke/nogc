/**
 * @time 2014年8月26日
 * @author sunke
 */
package com.cffex.nogc.serializable;

/**
 * @author sunke
 * @ClassName PojoSerializerFactory
 * @Description: PojoSerializer的工厂方法
 */
public class PojoSerializerFactory {
	
	public static enum PojoSerializerType{
		CSON;
	}
	private PojoSerializerFactory() {
	}

	public static PojoSerializable getpSerializer() {
		return new CSONPojoSerializer();
	}
	/**
	 * 根据类新构建pojo的序列化器
	 * @param type 序列化器的类型
	 * @return 序列化器
	 */
	public static PojoSerializable getpSerializerByType(PojoSerializerType type) {
		switch (type) {
		case CSON:
			return new CSONPojoSerializer();
		default:
			return new CSONPojoSerializer();
		}
	}
}
