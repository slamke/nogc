package com.cffex.nogc.memory.buffer;
/**
 * @author sunke
 * @ClassName Buffer
 * @Description: segment中的buffer区
 */
public class Buffer {
	/**
	 * buffer区相对于segment的offset 
	 */
	public static final int OFFSET = 0;
	
	/**
	 * buffer区的剩余空闲空间的阈值，低于这个空间时，需要进行merge
	 */
	public static final int THRESHOLD = 256;
public static final int CAPACITY = 0;
public int state;
private int length; 
public void updateLength(){
	
}
public int getState() {
	return state;
}
public void setState(int state) {
	this.state = state;
}
public int[] getOffsetById(){
	return null;
}
public int getOffsetByLength(){ return 0;}
}
