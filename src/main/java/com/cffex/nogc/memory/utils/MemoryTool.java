/**
 * @time 2014年8月19日
 * @author sunke
 * @Description TODO
 */
package com.cffex.nogc.memory.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;

import sun.misc.Unsafe;

/**
 * @author sunke TaoZhou
 * @ClassName MemoryTool
 * @Description: 堆外内存统一申请和释放工具类
 */
public class MemoryTool {
	public final static Unsafe UNSAFE;
	public static long BYTES_OFFSET;
	//allocate memory
	static{
		try {
			@SuppressWarnings("ALL")
			Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
			theUnsafe.setAccessible(true);
			UNSAFE = (Unsafe) theUnsafe.get(null);
			BYTES_OFFSET = UNSAFE.arrayBaseOffset(byte[].class);
		} catch (Exception e) {
			throw new AssertionError(e);
		}
	}
	public static synchronized ByteBuffer allocate(int capacity) {
		ByteBuffer byteBuffer = null;
		byteBuffer = ByteBuffer.allocateDirect(capacity);
		return byteBuffer;
	}
	//free memory
	public static synchronized boolean free(ByteBuffer bytebuffer) {
		if(bytebuffer.isDirect()) {
		        try {
		            if(!bytebuffer.getClass().getName().equals("java.nio.DirectByteBuffer")) {
		                Field attField = bytebuffer.getClass().getDeclaredField("att");
		                attField.setAccessible(true);
		                bytebuffer = (ByteBuffer) attField.get(bytebuffer);
		            }
		            Method cleanerMethod = bytebuffer.getClass().getMethod("cleaner");
		            cleanerMethod.setAccessible(true);
		            Object cleaner = cleanerMethod.invoke(bytebuffer);
		            Method cleanMethod = cleaner.getClass().getMethod("clean");
		            cleanMethod.setAccessible(true);
		            cleanMethod.invoke(cleaner);
		            return true;
		        } catch(Exception e) {
		            try {
						throw new Exception("Could not destroy direct buffer ", e);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
		        }
		}
		return false;
	}
}
