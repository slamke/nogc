/**
 * @time 2014年8月26日
 * @author sunke
 */
package com.cffex.nogc.serializable;

import com.cffex.nogc.cson.access.EntitySchemaCache;
import com.cffex.nogc.cson.core.entityInterface.common.IEntitySchema;

/**
 * @author sunke
 * @ClassName PojoSerializerTool
 * @Description: 工具类
 */
public class PojoSerializerTool {
	private PojoSerializerTool() {
	}

	/**
	 * 通过propertyName获取该property再clazz中的index
	 * @param clazz 类型
	 * @param propertyName property的名称
	 * @return 存在该index，返回index；否则，返回-1
	 */
	public int getIndexByPropertyName(Class<?> clazz, String propertyName) {
		IEntitySchema schema = EntitySchemaCache.objectSchemaFactory(clazz);
		return schema.getID(propertyName);
	}
}
