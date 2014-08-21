package com.cffex.nogc.memory.data;

import com.cffex.nogc.memory.NoGcByteBuffer;
import com.cffex.nogc.memory.Segment;
import com.cffex.nogc.memory.utils.MemoryTool;

/**
 * @author Tao Zhou
 * @ClassName Data
 * @Description: Data数据
 */
public class Data {
	
	//阈值设置为256K
	public static final int THRESHOLD = 256;
	
	//segment 头部buffer 大小为128K
	public static int OFFSET  = 128*1024;
	
	//
	private NoGcByteBuffer nogcData;
	private int freespace;
	private int capacity;
	

	private long minId;
	private long maxId;
	private int count;
	
	
	protected Data(int capacity, int freespace, long maxId, long minId, int count,NoGcByteBuffer nogcData){
		this.capacity = capacity;
		this.freespace = freespace;
		this.maxId = maxId;
		this.minId = minId;
		this.count = count;
		this.nogcData = nogcData;
	}
	//
	protected int updateMetaData(){
		return 0;
	}
	//
	protected int getOffsetByLength(){
		return 0;
	}
	
	protected int getIndexRegionOffset(){
		return 0;
	}

	protected int getCapacity() {
		return capacity;
	}
	
//	protected void updateCapacity(){
//		this.capacity = (int) (this.capacity*Segment.DEFAULT_REACTOR);                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 
//	}
	
	protected void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	
	protected int getFreesapce() {
		return freespace;
	}

	protected void setFreesapce(int freesapce) {
		//this.freesapce = freesapce;
	}

	protected long getMinId() {
		return this.minId;
	}

	protected void setMinId(long minId) {
		this.minId = minId;
	}

	protected long getMaxId(){
		return this.maxId;
	}
	
	protected void setMaxId(long maxId) {
		this.maxId = maxId;
	}

	protected int getCount() {
		return count;
	}
	
	protected void updateCount(){
		this.count = this.count+1;
	}
	
	protected void setCount(int count) {
		this.count = count;
	}

	//read bytes
	protected byte[] getBytes(int length){
		return nogcData.getBytes(length);
	}
	protected byte[] getBytes(int offset, int length){
		return nogcData.getBytes(offset, length);
	}

	
	//write bytes;
	protected int copyBytes( int offset0, int length, int offset1){
		nogcData.copyBytes(offset0, length, offset1);
		return 1;
	}
	protected int putBytes(byte[] b, int offset){
		nogcData.putBytes(offset, b);
		return 1;
	}
	protected int putBytes(byte[] b){
		nogcData.putBytes( b);
		return 1;
	}
	
	
	
	
	//read byte
	protected byte getByte(){
		return nogcData.get();
	}
	protected byte geByte(int offset){
		return nogcData.get(offset);
	}
	//write byte
	protected void putByte(byte b){
		nogcData.put(b);
	}
	protected void putByte(int offset, byte b){
		nogcData.put(offset, b);
	}
	
	//read long
	protected long getLong(){
		return nogcData.getLong();
	}
	protected long getLong(int offset){
		return nogcData.getLong(offset);
  		
	}
	//write long
	protected void putLong(long value){
		nogcData.putLong(value);
	}
	protected void putLong(int offset, long value){
		nogcData.putLong(offset, value);
	}
	
	//read int
	protected int getInt(int offset){
		return nogcData.getInt(offset);
	}
	protected int getInt(){
		return nogcData.getInt();
	}
	
	//write int
	protected void putInt(int value){
		nogcData.putInt(value);
	}
	protected void putInt(Integer offset, int value){
		nogcData.putInt(offset, value);
	}

	protected int getDataStartOffset(){
		return Data.OFFSET;
	}
	protected int getDataEndOffset(){
		//找最后一个index
		int dataOffset = getInt(getIndexStartOffset()+8);
		int dataLength = getInt(dataOffset);
		return dataOffset+dataLength;
	}
	
	protected int getIndexEndOffset(){
		return getDataStartOffset()+getCapacity();
	}
	
	protected int getIndexStartOffset(){
		return getIndexEndOffset()-getCount()*12;
	}
	
	
	
}
