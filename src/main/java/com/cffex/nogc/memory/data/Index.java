
/**
 *@author  Tao Zhou
*@classname Index.java
*@date 下午3:40:22
*@description
 */
package com.cffex.nogc.memory.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

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
	 * @param minId
	 * @param maxId
	 * @param count
	 * @param indexStartOffset
	 * @param nogcIndex
	 */
	public Index(long minId, long maxId, int count, int indexStartOffset, NoGcByteBuffer nogcIndex) {
		// TODO Auto-generated constructor stub
		this.count = count;
		this.nogcIndex = nogcIndex;
		this.minId = minId;
		this.maxId = maxId;
		this.startOffset = indexStartOffset;
		this.endOffset = indexStartOffset + INDEX_ITEM_LENGTH*this.count;
	}
	/**
	 * @param index数组（id+offset），id按从大到小排列，和存储顺序一致
	 * @param addoffset
	 * @return
	 */
	protected byte[] update(byte[] index, int addoffset) {
		ByteBuffer buf = ByteBuffer.allocate(index.length).order(ByteOrder.LITTLE_ENDIAN);
		buf.put(buf);
		buf.flip();
		int size = index.length/12;
		byte[] b = new byte[index.length];
		byte[] intbyte = new byte[Index.OFFSET_LENGTH];
		for(int i = 0; i<size; i++){
			int newoffset = buf.getInt(i*Index.INDEX_ITEM_LENGTH+Index.ID_LENGTH)+addoffset;
			try {
				intbyte = objectToBytes(newoffset);
				System.arraycopy(index, Index.INDEX_ITEM_LENGTH*size, b, i*12, 8);
				System.arraycopy(intbyte, 0, b, i*12+8, 4);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return b;
	}
	protected int getStartOffset() {
		return startOffset;
	}
	protected int getEndOffset() {
		return endOffset;
	}
	protected long getMinId() {
		return nogcIndex.getLong(getIndexEndOffset()-INDEX_ITEM_LENGTH);
		//return minId;
	}
	protected long getMaxId() {
		return nogcIndex.getLong(getIndexStartOffset());
	}
	protected int getCount() {
		return count;
	}
	protected NoGcByteBuffer getNogcIndex() {
		return nogcIndex;
	}
	/**
	 * 
	 */
	protected void updateCount() {
		// TODO Auto-generated method stub
		this.count = this.count + 1;
		
	}

	protected int binarySearchById(long id){
		
		if(getCount() == 0|| id < getMinId() || id > getMaxId()){
			return -1;
		}else{
			int low = 0;   
		        int high = getCount()-1; 
		        while(low <= high) {   
		            int middle = (low + high)/2;   
		            int offset = getIndexEndOffset()-middle*Index.INDEX_ITEM_LENGTH;
		            if(id == getIdByOffset(offset)) {   
		                return offset;   
		            }else if(id <getLong(offset)) {   
		                high = middle - 1;   
		            }else {   
		                low = middle + 1;   
		            }  
		        }  
		        return -1; 
		}

	}
	
	
	/*
	 * 找到id在index区中插入的合适位置
	 */
	protected int findIdOffset(long id){
		if(getCount() == 0){
			return getIndexEndOffset();
		}else{
			if(id > getMaxId()){
				return getIndexStartOffset();
			}else if(id< getMinId()){
				return getIndexEndOffset();
			}else{
				int low = 0;   
			        int high = getCount()-1; 
			        while(low <= high) {   
			            int middle = (low + high)/2;   
			            int offset = getIndexEndOffset()-middle*Index.INDEX_ITEM_LENGTH;
			            if(id == getIdByOffset(offset)) {   
			                return offset;   
			            }else if(id <getIdByOffset(offset)) {   
			                high = middle - 1;   
			            }else {   
			                low = middle + 1;   
			            }  
			        }  
			        return getIndexEndOffset()-low*Index.INDEX_ITEM_LENGTH;
			}
		}
	}
	
	/**
	 * @param offset
	 * @return 
	 */
	private long getIdByOffset(int offset) {
		// TODO Auto-generated method stub
		return nogcIndex.getLong(offset-Index.INDEX_ITEM_LENGTH);
	}
	private int getIndexEndOffset(){
		return this.endOffset;
	}
	
	private int getIndexStartOffset(){
		return this.startOffset;
	}
	
	
	protected void copyLargerThanMaxIdIndex(int maxIdOffsetIndex, int minIdOffsetIndex, int indexLength,
			int dataOffsetIncrement) {
		// TODO Auto-generated method stub
		int startOffset = getIndexStartOffset();
		int copyLength = maxIdOffsetIndex-startOffset;
		byte[] largerThanMaxIndex = getBytes(startOffset, copyLength);
		update(largerThanMaxIndex, dataOffsetIncrement);
		int oldLength = minIdOffsetIndex - maxIdOffsetIndex;
		int desOffset = startOffset - (indexLength - oldLength);
		putBytes(largerThanMaxIndex, desOffset);
	}
	
	protected void insertIndex(IndexItem[] indexItems, int minIdDataOffset,
			int minIdIndexOffset) {
		for(int i = 0; i < indexItems.length; i = i + 1){
			indexItems[i].setOffset(indexItems[i].getOffset()+minIdDataOffset);
		}
		byte[] b = new byte[indexItems.length*Index.INDEX_ITEM_LENGTH];
		byte[] intbyte = new byte[Index.OFFSET_LENGTH];
		byte[] longbyte = new byte[Index.ID_LENGTH];
		//IndexItem[] 转为byte[] insert,index区是id从大到小(由于index从Segment的尾部开始)
		for(int i = indexItems.length - 1; i >=0; i--){
			try {
				longbyte = objectToBytes(indexItems[i].getId());
				intbyte = objectToBytes(indexItems[i].getOffset());
				System.arraycopy(longbyte, 0, b, i*12, 8);
				System.arraycopy(intbyte, 0, b, i*12+8, 4);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		int newIndexStartOffset = minIdIndexOffset - indexItems.length*Index.INDEX_ITEM_LENGTH;
		putBytes(b, newIndexStartOffset);
	}
	
	private byte[] objectToBytes(Object obj) throws IOException {  
	        ByteArrayOutputStream bout = new ByteArrayOutputStream();  
	        ObjectOutputStream out = new ObjectOutputStream(bout);  
	        out.writeObject(obj);  
	        out.flush();  
	        byte[] bytes = bout.toByteArray();  
	        bout.close();  
	        out.close();  
	        return bytes;  
	}  
	
	/**
	 * @param indexBytes
	 * @param minIdDataOffset
	 * @param minIdIndexOffset
	 */
	public void insertIndex(byte[] indexBytes, int minIdDataOffset,
			int minIdIndexOffset) {
		// TODO Auto-generated method stub
		byte[] tempIndexBytes = update(indexBytes, minIdDataOffset);
		int newIndexStartOffset = minIdIndexOffset - indexBytes.length;
		putBytes(tempIndexBytes, newIndexStartOffset);
		
	}
	
	/**
	 * @param offstList
	 * @param minIdDataOffset
	 * @param minIdIndexOffset
	 */
	public void insertIndex(List<IndexItem> offstList, int minIdDataOffset,
			int minIdIndexOffset) {
		// TODO Auto-generated method stub
		byte[] indexBytes = new byte[offstList.size()*Index.INDEX_ITEM_LENGTH];
		byte[] intbyte = new byte[Index.OFFSET_LENGTH];
		byte[] longbyte = new byte[Index.ID_LENGTH];
		for(int i = 0; i<offstList.size(); i++){
			try {
				longbyte = objectToBytes(offstList.get(i).getId());
				intbyte = objectToBytes(offstList.get(i).getOffset()+minIdDataOffset);
				System.arraycopy(longbyte, 0, indexBytes, i*12, 8);
				System.arraycopy(intbyte, 0, indexBytes, i*12+8, 4);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//byte[] tempIndexBytes = update(indexBytes, minIdDataOffset);
		int newIndexStartOffset = minIdIndexOffset - indexBytes.length;
		putBytes(indexBytes, newIndexStartOffset);
		
	}
	//read bytes
	private byte[] getBytes(int length){
		return nogcIndex.getBytes(length);
	}
	private byte[] getBytes(int offset, int length){
		return nogcIndex.getBytes(offset, length);
	}

		
	//write bytes;
	private int copyBytes( int offset0, int length, int offset1){
		nogcIndex.copyBytes(offset0, length, offset1);
		return 1;
	}
	private int putBytes(byte[] b, int offset){
		nogcIndex.putBytes(offset, b);
		return 1;
	}
	private int putBytes(byte[] b){
		nogcIndex.putBytes( b);
		return 1;
	}
		
		
		
		
	//read byte
	private byte getByte(){
		return nogcIndex.get();
	}
	private byte getByte(int offset){
		return nogcIndex.get(offset);
	}
	//write byte
	private void putByte(byte b){
		nogcIndex.put(b);
	}
	private void putByte(int offset, byte b){
		nogcIndex.put(offset, b);
	}
		
	//read long
	private long getLong(){
		return nogcIndex.getLong();
	}
	private long getLong(int offset){
		return nogcIndex.getLong(offset);
	  		
	}
	//write long
	private void putLong(long value){
		nogcIndex.putLong(value);
	}
	private void putLong(int offset, long value){
		nogcIndex.putLong(offset, value);
	}
		
	//read int
	private int getInt(int offset){
		return nogcIndex.getInt(offset);
	}
	private int getInt(){
		return nogcIndex.getInt();
	}
		
	//write int
	private void putInt(int value){
		nogcIndex.putInt(value);
	}
	private void putInt(Integer offset, int value){
		nogcIndex.putInt(offset, value);
	}
	/**
	 * @param minIndexOffset
	 * @param maxIndexOffset
	 * @param minDataStartOffset 
	 * @return
	 */
	public List<IndexItem> getOffsetList(int minIndexOffset,
			int maxIndexOffset, int minDataStartOffset) {
		int size = (minIndexOffset - maxIndexOffset)/Index.INDEX_ITEM_LENGTH+1;
		List<IndexItem> resultIndexItems = new ArrayList<IndexItem>();
		for(int i = 0; i < size; i++){
			IndexItem indexItem = new IndexItem(this.getLong(minIndexOffset-size*Index.INDEX_ITEM_LENGTH),
					this.getInt(minIndexOffset-size*Index.INDEX_ITEM_LENGTH+Index.ID_LENGTH)-minDataStartOffset);
			resultIndexItems.add(indexItem);
		}
		// TODO Auto-generated method stub
		return resultIndexItems;
	}


	
}

