package com.cffex.nogc.memory.buffer.exception;

/**
 * 
 * @author sunke
 * @ClassName BufferLogException
 * @Description: BufferLogException-->bufferlog操作中自定义的exception
 */
public class BufferLogException extends Exception {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -7827689317081689321L;

	/**
	 * @param message
	 */
	public BufferLogException(String message) {
		super(message);
	}

}
