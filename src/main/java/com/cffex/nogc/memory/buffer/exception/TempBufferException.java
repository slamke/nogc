/**
 * @time 2014年8月25日
 * @author sunke
 */
package com.cffex.nogc.memory.buffer.exception;

/**
 * @author sunke
 * @ClassName TempBufferException
 * @Description: tempBuffer类操作中定义的exception。 
 */
public class TempBufferException extends Exception {



	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 3739275575779568794L;

	/**
	 * 构造函数
	 * @param message
	 */
	public TempBufferException(String message) {
		super(message);
	}

}
