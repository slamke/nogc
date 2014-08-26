/**
 *@author  Tao Zhou
*@classname IndexItem.java
*@date 下午4:52:19
*@description
 */
package com.cffex.nogc.memory.data;

/**
 * @author zhou
 *
 */
public class IndexItem {
	private long id;
	private int offset;
	public IndexItem(){
		
	}
	public IndexItem(long id, int offset){
		this.id = id;
		this.offset = offset;
	}
	public long getId() {
		return id;
	}
	public int getOffset() {
		return offset;
	}
	

}
