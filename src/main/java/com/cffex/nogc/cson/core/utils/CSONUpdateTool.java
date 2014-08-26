/**
 * @time 2014年8月26日
 * @author sunke
 */
package com.cffex.nogc.cson.core.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

import com.cffex.nogc.cson.access.EntitySchemaCache;
import com.cffex.nogc.cson.core.CSONDocument;

/**
 * @author sunke
 * @ClassName CSONUpdateTool
 * @Description: 将完整的cson数据段，与property更新的operation进行merge 
 */
public class CSONUpdateTool {
	private CSONUpdateTool(){}
	/**
	 * 将完整的cson数据段，与property更新的operation进行merge 
	 * @param clazz cson所属的class信息
	 * @param csonBuffer 完成的cson数据
	 * @param updateList 更新operation
	 * @return position归零后的bytebuffer
	 */
	public static ByteBuffer merge(Class<?> clazz,ByteBuffer csonBuffer,List<Tuple<Integer, Object>> updateList){
		CSONDocument document = new CSONDocument(EntitySchemaCache.objectSchemaFactory(clazz), csonBuffer);
		if (updateList != null) {
			for (Tuple<Integer, Object> tuple : updateList) {
				document.setValue(tuple.first, tuple.second);
			}
		}
		ByteBuffer buffer = ByteBuffer.wrap(new byte[4096]).order(ByteOrder.LITTLE_ENDIAN);
		ByteBuffer result = document.toByteBuffer(buffer);
		result.rewind();
		return result;
	}
}
