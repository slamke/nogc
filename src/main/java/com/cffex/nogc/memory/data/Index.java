/**
 *@author  Tao Zhou
*@classname Index.java
*@date 下午3:40:22
*@description
 */
package com.cffex.nogc.memory.data;

import com.cffex.nogc.memory.NoGcByteBuffer;

/**
 * @author zhou
 *
 */
public class Index {
	public static final int INDEX_ITEM_LENGTH = 12;
	public static final int ID_LENGTH = 8;
	public static final int OFFSET_LENGTH = 4;
	private int startOffset;
	private int endOffset;
	private long minId;

	private long maxId;
	private int count;
	//private Object[] indexObjects = null;
	private NoGcByteBuffer nogcIndex;
	/**
	 * @param minId2 
	 * @param maxId2 
	 * @param count
	 * @param indexStartOffset
	 * @param nogcIndex
	 */
	public Index(long maxId2, long minId2, int count, int indexStartOffset, NoGcByteBuffer nogcIndex) {
		// TODO Auto-generated constructor stub
		this.count = count;
		this.nogcIndex = nogcIndex;
		
		this.startOffset = indexStartOffset;
		this.endOffset = indexStartOffset + INDEX_ITEM_LENGTH*this.count;
//		indexObjects = new Object[this.count*2];
//		for(int i = 0; i < this.count; i++){
//			indexObjects[i*2] = this.nogcIndex.getLong(startOffset+INDEX_ITEM_LENGTH*i);
//			indexObjects[i*2+1] = this.nogcIndex.getLong(startOffset+INDEX_ITEM_LENGTH*i+ID_LENGTH);
//		}
	}
	/**
	 * @param index
	 * @param addoffset
	 * @return
	 */
	public byte[] update(byte[] index, int addoffset) {
		int size = index.length/12;
		for(int i = 0; i<size; i++){
			int newoffset = nogcIndex.getInt(size*INDEX_ITEM_LENGTH+ID_LENGTH)+addoffset;
			index[12*i] = (byte) ((newoffset >> 24) & 0xFF);
			index[12*i+1] = (byte) ((newoffset >> 16) & 0xFF);
			index[12*i+2] = (byte) ((newoffset >> 8)  & 0xFF);
			index[12*i+3] = (byte) (newoffset& 0xFF);
		}
		return index;
	}
	public int getStartOffset() {
		return startOffset;
	}
	public int getEndOffset() {
		return endOffset;
	}
	public long getMinId() {
		return minId;
	}
	public long getMaxId() {
		return maxId;
	}
	public int getCount() {
		return count;
	}
	public NoGcByteBuffer getNogcIndex() {
		return nogcIndex;
	}
	/**
	 * 
	 */
	public void updateCount() {
		// TODO Auto-generated method stub
		this.count = this.count + 1;
		
	}

}
