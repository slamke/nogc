/**
 * @time 2014年8月26日
 * @author sunke
 */
package com.cffex.nogc.memory.buffer.exception;


/**
 * @author sunke
 * @ClassName TempBufferException
 * @Description: Buffer merge过程中的自定义的exception。 
 */
public class IllegalBufferMergeException extends Exception {
	
	
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -1438613843573647391L;

	/**
	 * 构造函数
	 * @param message
	 */
	public IllegalBufferMergeException(String message) {
		super(message);
	}
}
