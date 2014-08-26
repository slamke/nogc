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
	public static final int OFFSET  = 128*1024;
	
	//
	private NoGcByteBuffer nogcData;
	private int freespace;
	private int capacity;
	


	private Index index;
	public static final int DATA_CSON_LENGTH = 4;
	
	protected Data(int capacity, int freespace, long minId, long maxId, int count,NoGcByteBuffer nogcData){
		this.capacity = capacity;
		this.freespace = freespace;
		this.nogcData = nogcData;
		int indexStartOffset = Data.OFFSET + this.capacity - Index.INDEX_ITEM_LENGTH*count;
		this.index = new Index(minId, maxId, count, indexStartOffset, nogcData);
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
		return this.index.getMinId();
	}

	protected long getMaxId(){
		return this.index.getMaxId();
	}

	
	protected Index getIndex(){
		return this.index;
	}
	
	protected void updateCount(){
		this.index.updateCount();
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
		int dataOffset = getInt(getIndexStartOffset()+Index.ID_LENGTH);
		int dataLength = getInt(dataOffset);
		return dataOffset+dataLength+DATA_CSON_LENGTH;
	}
	
	protected int getIndexEndOffset(){
		return getDataStartOffset()+getCapacity();
	}
	
	protected int getIndexStartOffset(){
		return getIndexEndOffset()-getCount()*Index.INDEX_ITEM_LENGTH;
	}
	/**
	 * @param offset2
	 * @return
	 */
	public byte[] getDataByOffset(int offset) {
		// TODO Auto-generated method stub
		int length = this.getInt(offset);
		return this.getBytes(offset,DATA_CSON_LENGTH + length);

	}
	
	/**
	 * @param offset2
	 * @return
	 */
	public byte[] getDatas(int startOffset, int endOffset) {
		// TODO Auto-generated method stub
		return getBytes(startOffset, endOffset-startOffset+1);

	}
	
	/**
	 * @param offset index的offset
	 * @return
	 */
	public int getDataItemStartOffset(int offset) {
		// TODO Auto-generated method stub
		int result = this.getInt(offset-Index.OFFSET_LENGTH);
		return result;
	}

	/**
	 * @param offset data开始的offset
	 * @return
	 */
	public int getDataItemEndOffset(int offset) {
		// TODO Auto-generated method stub
		int length = this.getInt(offset);
		int result = offset+DATA_CSON_LENGTH+length;
		return result;
	}
	/**
	 * @return
	 */
	public int getCount() {
		// TODO Auto-generated method stub
		return this.index.getCount();
	}
	
	protected void copyLargerThanMaxIdData(int maxIdOffsetIndex,
			int dataLength, int dataOffsetIncrement) {
		int maxIdOffsetData = getInt(maxIdOffsetIndex-Index.OFFSET_LENGTH);
		int copyLength = getDataEndOffset() - maxIdOffsetData;
		int desOffset = maxIdOffsetData + dataOffsetIncrement;
		copyBytes(maxIdOffsetData, copyLength, desOffset);
		// TODO Auto-generated method stub
		
	}
	/**
	 * @param minId
	 */
	protected void setMinId(long minId) {
		// TODO Auto-generated method stub
		this.index.setMinId(minId);
	}
	/**
	 * @param maxId
	 */
	protected void setMaxId(long maxId) {
		// TODO Auto-generated method stub
		this.index.setMaxId(maxId);
		
	}
	/**
	 * @param minIdOffsetIndex
	 * @return
	 */
	protected int getDataOffsetByIndexOffset(int minIdOffsetIndex) {
		// TODO Auto-generated method stub
		return this.getInt(minIdOffsetIndex-12);

	}
	
	
}
