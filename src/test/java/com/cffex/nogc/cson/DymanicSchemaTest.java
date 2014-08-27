/**
 * @time 2014年8月26日
 * @author sunke
 */
package com.cffex.nogc.cson;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.ArrayList;

import com.cffex.nogc.cson.access.DynamicSchema;
import com.cffex.nogc.cson.core.CSONDocument;
import com.cffex.nogc.cson.core.entityInterface.common.IElementType;
import com.cffex.nogc.cson.core.types.CSONTypesArray;

/**
 * @author sunke
 * @ClassName DymanicSchemaTest
 * @Description: TODO 
 */
public class DymanicSchemaTest {
	
	@org.junit.Test
 public void DynamicSchemaUsecase(){
	 DynamicSchema schema = new DynamicSchema("Test");
	 ArrayList<String> propertyList = new ArrayList<String>();
	 ArrayList<IElementType> propertyTypes = new ArrayList<IElementType>();
	 propertyList.add("name");
	 propertyList.add("age");
	 propertyTypes.add(CSONTypesArray.typeFactory(String.class));
	 propertyTypes.add(CSONTypesArray.typeFactory(Integer.class));
	 schema.setPropertyList(propertyList);
	 schema.setPropertyTypeList(propertyTypes);
	 CSONDocument document = new CSONDocument(schema, null);
	 document.setValue(0, "July");
	 document.setValue(1, 12);
	 ByteBuffer buffer = ByteBuffer.wrap(new byte[4096]).order(ByteOrder.LITTLE_ENDIAN);
	 ByteBuffer outBuffer = document.toByteBuffer(buffer);
	 if (buffer == outBuffer) {
		System.out.println("yes");
	}
	 outBuffer.flip();
	 System.out.println("length:"+buffer.getInt());
	 System.out.println("len:"+buffer.getInt());
	 System.out.println("limit:"+buffer.limit());
	 System.out.println("July".getBytes(Charset.forName("UTF-8")).length);
 }
}
