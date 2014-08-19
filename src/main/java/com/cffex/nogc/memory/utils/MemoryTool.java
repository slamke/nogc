/**
 * @time 2014年8月19日
 * @author sunke
 * @Description TODO
 */
package com.cffex.nogc.memory.utils;

import java.nio.ByteBuffer;

/**
 * @author sunke
 * @ClassName MemoryTool
 * @Description: 堆外内存统一申请和释放工具类
 */
public class MemoryTool {
	public static synchronized ByteBuffer allocate(int capacity) {
		return null;
	}

	public static synchronized boolean free(ByteBuffer buffer) {
		return false;
	}
}
