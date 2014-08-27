/**
 *@author  Tao Zhou
*@classname DataItem.java
*@date 下午3:40:27
*@description
 */
package com.cffex.nogc.memory.data;

import java.nio.ByteBuffer;

import com.cffex.nogc.cson.core.utils.CSONHelper;
import com.cffex.nogc.serializable.PojoSerializable;
import com.cffex.nogc.serializable.PojoSerializerFactory;

/**
 * @author zhou
 *
 */
public class DataItem {
	private long id;
	private byte[] value;
	private String schemaKey;
	public DataItem(long id, byte[] value,  String schemaKey){
		this.id = id;
		this.value = value;
		this.schemaKey = schemaKey;
	}
	public String getSchemaKey() {
		return schemaKey;
	}
	public void setSchemaKey(String schemaKey) {
		this.schemaKey = schemaKey;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public byte[] getValue() {
		return value;
	}
	public void setValue(byte[] value) {
		this.value = value;
	}
	public byte[] getValue(int index) {
		// TODO Auto-generated method stub
		try {
			return CSONHelper.getPropertyRawValueByIndex(toBytebuffer(), index, Class.forName(getSchemaKey()));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public ByteBuffer toBytebuffer(){
		PojoSerializable tool = PojoSerializerFactory.getSerializer();
		ByteBuffer byteBuffer = tool.writeObjectToByteBuffer(this);
		return byteBuffer;
	}
}
