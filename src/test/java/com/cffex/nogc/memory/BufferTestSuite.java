/**
 * @time 2014年8月19日
 * @author sunke
 * @Description TODO
 */
package com.cffex.nogc.memory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sun.nio.ch.DirectBuffer;

import com.cffex.nogc.cson.core.utils.CSONHelper;
import com.cffex.nogc.enumeration.IsolationType;
import com.cffex.nogc.memory.buffer.Buffer;
import com.cffex.nogc.memory.buffer.BufferExcerpt;
import com.cffex.nogc.memory.buffer.BufferLog;
import com.cffex.nogc.memory.buffer.BufferLog.BufferLogType;
import com.cffex.nogc.memory.buffer.BufferOperatable;
import com.cffex.nogc.memory.utils.MemoryTool;

/**
 * @author sunke
 * @ClassName BufferTestSuite
 * @Description: TODO 
 */
public class BufferTestSuite {

	private BufferOperatable operatable;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		SegmentExcerpt excerpt = new SegmentExcerpt(IsolationType.RESTRICT);
		operatable = excerpt.getBufferOperatable();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	/**
	 * 测试bufferlog的append和读取(整体更新和插入)-->测试通过。
	 */
	@Test
	public void bufferPropertyUpdateAndGet(){
		Pojo insertPojo = new Pojo(12, "hello world!", 1234);
		ByteBuffer insertBuffer = CSONHelper.serializeObjectToCSON(insertPojo, Pojo.class);
		int length = insertBuffer.limit();
		byte[] content = new byte[length];
		insertBuffer.get(content);
		BufferLog log = new BufferLog(BufferLogType.INSERT, 123, content);

		ByteBuffer logBuffer = log.toBytebuffer();
		int datalength = logBuffer.limit();
		
		boolean lockResult = operatable.tryLockWithLength(datalength);
		System.out.println("datalength:"+datalength);
		if (lockResult) {
			operatable.appendOperation(log);
		}
		
		insertPojo.setAge(24);
		insertPojo.setId(345);
		insertPojo.setName("oh my god!");
		byte[] newValue = CSONHelper.serializeObjectToCSONBinary(insertPojo, Pojo.class);
		BufferLog log2 = new BufferLog(BufferLogType.UPDATE_ALL, 123, newValue);
		ByteBuffer logBuffer2 = log2.toBytebuffer();
		int newLength = logBuffer2.limit();
		System.out.println("newLength:"+newLength);
		boolean lockAgain = operatable.tryLockWithLength(newLength);
		if (lockAgain) {
			operatable.appendOperation(log2);
		}
		
		insertPojo.setAge(24);
		insertPojo.setId(8888);
		insertPojo.setName("updated...");
		byte[] newName = "updated...".getBytes(Charset.forName("utf-8"));
		for (int i = 0; i < newName.length; i++) {
			System.out.print(" "+newName[i]);
		}
		
		ByteBuffer newBuffer = CSONHelper.serializeObjectToCSON(insertPojo, Pojo.class);
		byte[] idProperty = CSONHelper.getPropertyRawValueByIndex(newBuffer, 2, Pojo.class);
		
		System.out.println("idProperty:"+idProperty.toString());
		newBuffer.rewind();
		byte[] nameProperty = CSONHelper.getPropertyRawValueByIndex(newBuffer, 1, Pojo.class);
		
		BufferLog propertyIdLog = new BufferLog(BufferLogType.UPDATE_PROPERTY, 123, idProperty, 2, Pojo.class.toString()); 
		
		BufferLog propertyNameLog = new BufferLog(BufferLogType.UPDATE_PROPERTY, 123, nameProperty, 1, Pojo.class.toString()); 
		
		lockResult = operatable.tryLockWithLength(propertyIdLog.toBytebuffer().limit());
		if (lockResult) {
			operatable.appendOperation(propertyIdLog);
		}
		
		lockResult = operatable.tryLockWithLength(propertyNameLog.toBytebuffer().limit());
		if (lockResult) {
			operatable.appendOperation(propertyNameLog);
		}
		
		byte[] agebinary = operatable.getPropertyById(123, 0, Pojo.class);
		
		byte[] idbinary = operatable.getPropertyById(123, 2, Pojo.class);
		byte[] namebinary = operatable.getPropertyById(123, 1, Pojo.class);
		ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[100]).order(ByteOrder.LITTLE_ENDIAN);
		byteBuffer.put(agebinary);
		System.out.println("age:"+byteBuffer.getInt(1));
		byteBuffer.rewind();
		byteBuffer.put(idbinary);
		System.out.println("id:"+byteBuffer.getInt(1));
		byteBuffer.rewind();
		byteBuffer.put(namebinary);
		byte[] value = new byte[namebinary.length-5];
		for (int i = 0; i < value.length; i++) {
			value[i] = namebinary[i+5];
			System.out.print(" "+value[i]);
		}
		
		System.out.println("name:"+new String(value, Charset.forName("utf-8")));
		
	}
	
	
	/**
	 * 测试bufferlog的append和读取(整体更新和插入)-->测试通过。
	 */
	@Test
	public void bufferWholeInsertAndGet(){
		Pojo insertPojo = new Pojo(12, "hello world!", 1234);
		ByteBuffer insertBuffer = CSONHelper.serializeObjectToCSON(insertPojo, Pojo.class);
		int length = insertBuffer.limit();
		byte[] content = new byte[length];
		insertBuffer.get(content);
		BufferLog log = new BufferLog(BufferLogType.INSERT, 123, content);

		ByteBuffer logBuffer = log.toBytebuffer();
		int datalength = logBuffer.limit();
		
		boolean lockResult = operatable.tryLockWithLength(datalength);
		System.out.println("datalength:"+datalength);
		if (lockResult) {
			operatable.appendOperation(log);
		}
		
		insertPojo.setAge(24);
		insertPojo.setId(345);
		insertPojo.setName("oh my god!");
		byte[] newValue = CSONHelper.serializeObjectToCSONBinary(insertPojo, Pojo.class);
		BufferLog log2 = new BufferLog(BufferLogType.UPDATE_ALL, 123, newValue);
		ByteBuffer logBuffer2 = log2.toBytebuffer();
		int newLength = logBuffer2.limit();
		System.out.println("newLength:"+newLength);
		boolean lockAgain = operatable.tryLockWithLength(newLength);
		if (lockAgain) {
			operatable.appendOperation(log2);
		}
		
		List<BufferLog> getList = operatable.getById(123);
		assert getList.size() == 2;
		BufferLog get1 = getList.get(0);
		ByteBuffer buffer1 = ByteBuffer.wrap(get1.getValue()).order(ByteOrder.LITTLE_ENDIAN);
		Pojo pojo1 = (Pojo)CSONHelper.readCSONToObject(buffer1, Pojo.class);
		System.out.println(pojo1.toString());
		BufferLog get2 = getList.get(1);
		ByteBuffer buffer2 = ByteBuffer.wrap(get2.getValue()).order(ByteOrder.LITTLE_ENDIAN);
		Pojo pojo2 = (Pojo)CSONHelper.readCSONToObject(buffer2, Pojo.class);
		System.out.println(pojo2.toString());
	}
}
