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
import com.cffex.nogc.cson.core.entityInterface.common.IElementType;
import com.cffex.nogc.cson.core.entityInterface.common.IEntitySchema;
import com.cffex.nogc.cson.core.types.CSONTypesArray;
import com.cffex.nogc.memory.utils.PotentialProblem;

/**
 * @author sunke
 * @ClassName CSONUpdateTool
 * @Description: 将完整的cson数据段，与property更新的operation进行merge 
 */
public class CSONMergeTool {
	private CSONMergeTool(){}
	/**
	 * 将完整的cson数据段，与property更新的operation进行merge 
	 * @param clazz cson所属的class信息
	 * @param csonBuffer 完整的cson数据
	 * @param updateOperation 更新operation元组(index,data)  data中不包含typecode
	 * @return position归零后的bytebuffer:flip
	 */
	@PotentialProblem(reason="类型比较多，需要进行细致的测试")
	public static ByteBuffer merge(Class<?> clazz,ByteBuffer csonBuffer,Tuple<Integer, byte[]> updateOperation){
		if (updateOperation != null) {
			CSONDocument document = new CSONDocument(EntitySchemaCache.objectSchemaFactory(clazz), csonBuffer);
			IEntitySchema schema = EntitySchemaCache.objectSchemaFactory(clazz);
			//获取property的type
			IElementType type= schema.getElementType(updateOperation.first);
			//获取property的typecode
			byte typecode = CSONTypesArray.typeCodeOf(type.getClass());
			//将byte[]根据bytecode转换为object
			Object object = GeneralEntityToCSON.readRawValue2Obj(updateOperation.second, true,typecode);
			//根据bytecode无法转换时，使用serializer进行转换为object
			if (object == null) {
				GeneralEntityToCSON serializer = EntitySerializerCache.getEntityToCSON(type.getClass());
				ByteBuffer bb = ByteBuffer.wrap(updateOperation.second).order(ByteOrder.LITTLE_ENDIAN);
				object = serializer.readCSONToObject(bb);
			}
			document.setValue(updateOperation.first, object);
			ByteBuffer buffer = ByteBuffer.wrap(new byte[4096]).order(ByteOrder.LITTLE_ENDIAN);
			ByteBuffer result = document.toByteBuffer(buffer);
			result.flip();
			return result;
		}
		return csonBuffer;
	}
}
