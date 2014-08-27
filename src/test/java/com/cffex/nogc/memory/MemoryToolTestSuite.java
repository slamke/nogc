/**
 * @time 2014年8月21日
 * @author sunke
 */
package com.cffex.nogc.memory;

import java.lang.reflect.Field;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sun.misc.Unsafe;
import sun.nio.ch.DirectBuffer;

import com.cffex.nogc.memory.utils.MemoryTool;

/**
 * @author sunke
 * @ClassName MemoryToolTest
 * @Description: TODO 
 */
public class MemoryToolTestSuite {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	
	@Test
	public void copyFormDirectbuffer() {
		byte[] data = new byte[]{1,2,3,4,5,6,7,8,9,0};
		DirectBuffer a = MemoryTool.allocate(10);
		NoGcByteBuffer aBuffer = new NoGcByteBuffer(0, 10, a);
		aBuffer.putBytes(data);
		DirectBuffer b = MemoryTool.allocate(10);
		try {
			@SuppressWarnings("all")
			Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
			theUnsafe.setAccessible(true);
			Unsafe UNSAFE = (Unsafe) theUnsafe.get(null);
			long BYTES_OFFSET = UNSAFE.arrayBaseOffset(byte[].class);
			UNSAFE.copyMemory(data, BYTES_OFFSET, null, b.address(), 10);
			NoGcByteBuffer bBuffer = new NoGcByteBuffer(0, 10, b);
			byte[] value = bBuffer.getBytes(10);
			for (int i = 0; i < value.length; i++) {
				System.out.println(value[i]);
			}
					
		} catch (Exception e) {
			throw new AssertionError(e);
		}
	}

}
