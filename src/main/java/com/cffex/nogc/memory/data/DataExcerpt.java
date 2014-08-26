package com.cffex.nogc.memory.data;
/**
 * @author Tao Zhou
 * @ClassName DataExcerpt
 * @Description: Data操作接口
 */
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.text.DefaultEditorKit.CopyAction;

import com.cffex.nogc.enumeration.IsolationType;
import com.cffex.nogc.memory.NoGcByteBuffer;
import com.cffex.nogc.memory.Segment;
import com.cffex.nogc.memory.SegmentExcerpt;
import com.cffex.nogc.memory.SegmentOperateable;
import com.cffex.nogc.memory.buffer.Buffer;
import com.cffex.nogc.memory.data.exception.DataException;
import com.cffex.nogc.memory.utils.MemoryTool;
import com.sun.beans.editors.ByteEditor;

public class DataExcerpt extends AbstractDataExcerpt{

	//private region
	
	
	public DataExcerpt(SegmentExcerpt segmentExcerpt, Data data){
		super(segmentExcerpt, data);

	}
	public DataExcerpt(SegmentExcerpt segmentExcerpt, NoGcByteBuffer nogcData){
		super(segmentExcerpt, nogcData);

	}
	public Data getData() {
		return data;
	}
	//private methods to implement functions in DataOperateable
	
	
	
	
	

	protected byte[] getDataById(long id) {
		// TODO Auto-generated method stub
		int offset = getOffsetById(id);
		if(offset < 0){
			return null;
		}else{
			return this.data.getDataByOffset(offset);
		}
	}
	

	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.data.AbstractDataExcerpt#getDataPropertyById(long, int)
	 */
	@Override
	protected byte[] getDataPropertyById(long id, int index) {
		// TODO Auto-generated method stub
		int offset = getOffsetById(id);
		if(offset < 0){
			return null;
		}else{
			//todo
			return this.data.getDataByOffset(offset);
		}
	}
	
	/*
	 * 在Index区二分查找
	 * @param id 查找的id
	 * @return offset的值
	 */
	protected int getOffsetById(long id) {
		int offset = binarySearchById(id);
		if(offset < 0){
			return -1;
		}else{
			return this.data.getDataOffset(offset);
		}
		
	}
	
	/*
	 * index(id+offset)
	 * 找到index中id对应offset的位置
	 */
	protected int binarySearchById(long id){
		
		if(data.getCount() == 0|| id < data.getMinId() || id > data.getMaxId()){
			return -1;
		}else{
			int low = 0;   
		        int high = data.getCount()-1; 
		        while(low <= high) {   
		            int middle = (low + high)/2;   
		            int offset = this.data.getIndexEndOffset()-middle*Index.INDEX_ITEM_LENGTH;
		            if(id == this.data.getIdByOffset(offset)) {   
		                return offset;   
		            }else if(id <this.data.getLong(offset)) {   
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
		if(data.getCount() == 0){
			return this.data.getIndexEndOffset();
		}else{
			if(id > this.data.getMaxId()){
				return this.data.getIndexStartOffset();
			}else if(id< this.data.getMinId()){
				return this.data.getIndexEndOffset();
			}else{
				int low = 0;   
			        int high = this.data.getCount()-1; 
			        while(low <= high) {   
			            int middle = (low + high)/2;   
			            int offset = this.data.getIndexEndOffset()-middle*Index.INDEX_ITEM_LENGTH;
			            if(id == this.data.getIdByOffset(offset)) {   
			                return offset;   
			            }else if(id <this.data.getIdByOffset(offset)) {   
			                high = middle - 1;   
			            }else {   
			                low = middle + 1;   
			            }  
			        }  
			        return this.data.getIndexEndOffset()-low*Index.INDEX_ITEM_LENGTH;
			}
		}
	}

	
	
	
	//object数组id,index,id,index
	protected Object[] GetIndexRegion(long minid, long maxid){
		
		int offset1 = findIdOffset(minid);
		int offset2 = findIdOffset(maxid);
		int size = (offset1-offset2)/12;
		Object[] result = new Object[size*2];
		for(int i = 0; i < size; i++){
			result[(size-1)*2] = this.data.getLong(offset1-8);
			result[(size-1)*2+1] = this.data.getInt(offset1-4);
		}
		return result;
	}
	protected int setIndexRegion(){
		return 0;
	}
	@SuppressWarnings("unused")
	protected int checkFreeSpace(){
		if(data.getFreesapce() < Data.THRESHOLD){
			return 0;
		}else{
			return 1;
		}
	}
	
	protected SegmentExcerpt resizeData(){
		
		int newCapacity = (int) (data.getCapacity()*Segment.DEFAULT_REACTOR);
		int newFreeSpace = newCapacity - (this.data.getCapacity()-this.data.getFreesapce());
		Segment newSegment = new Segment(newCapacity);
		SegmentExcerpt newsegmentExcerpt = new SegmentExcerpt(IsolationType.RESTRICT);
		NoGcByteBuffer newNoGcData = new NoGcByteBuffer(Buffer.CAPACITY, newCapacity, newsegmentExcerpt.getSegment().getByteBuffer());
		
		Data newData = new Data(newCapacity, newFreeSpace, this.data.getMaxId(), this.data.getMinId(), this.data.getCount(),newNoGcData);
		DataExcerpt newDataExcerpt = new DataExcerpt(newsegmentExcerpt,newData);
		
		int segmentStartOffset = 0;
		
		int indexStartOffset = this.data.getIndexStartOffset();
		int indexEndOffset = this.data.getIndexEndOffset();
		int indexLength = indexEndOffset - indexStartOffset;
		
		int dataStartOffset = this.data.getDataStartOffset();
		int dataEndOffset = this.data.getDataEndOffset();
		int bufferAndDataLength = dataEndOffset - dataStartOffset;
		
		int newSegmentStartOffset = 0;
		int newIndexEndOffset = newData.getIndexEndOffset();
		int newIndexStartOffset = newIndexEndOffset - indexLength;

		return newsegmentExcerpt;
	}
	
	/**
	 * @return
	 */

	//更新index中的offset值
	private byte[] updateIndex(byte[] index, int addoffset){
		return this.data.getIndex().update(index ,addoffset);
		
		
	}
	private int updateIndexData(byte[] index, int addoffset){
		return 0;
	}
	
	
	

	




	



	@Override
	protected byte[] getData0(long minId, long maxId) {
		// TODO Auto-generated method stub
		int minIndexOffset = findIdOffset(minId);//找到最小id的index offset
		int maxIndexOffset = findIdOffset(maxId);//找到最大id的index offset
		int minDataStartOffset = this.data.getInt(minIndexOffset-12);//最小id的data offset
		int maxDataStartOffset = this.data.getInt(maxIndexOffset-12);//最大id的data offset
		//最后一个data结束的位置 = data offset+cson length(4) + length
		int maxDataEndOffset = this.data.getInt(maxDataStartOffset) + 4 +this.data.getInt(this.data.getInt(maxDataStartOffset));
		byte[] result = this.data.getBytes(minDataStartOffset, maxDataEndOffset - minDataStartOffset+1);
		return result;
	}
	
	@Override
	protected HashMap<Long, byte[]> getData(long minId, long maxId) {
		// TODO Auto-generated method stub
		int minIndexOffset = findIdOffset(minId);//找到最小id的index offset
		int maxIndexOffset = findIdOffset(maxId);//找到最大id的index offset
		int minDataStartOffset = this.data.getInt(minIndexOffset-12);//最小id的data offset
		int maxDataStartOffset = this.data.getInt(maxIndexOffset-12);//最大id的data offset
		//最后一个data结束的位置 = data offset+cson length(4) + length
		int maxDataEndOffset = this.data.getInt(maxDataStartOffset) + 4 +this.data.getInt(this.data.getInt(maxDataStartOffset));
		byte[] result = this.data.getBytes(minDataStartOffset, maxDataEndOffset - minDataStartOffset+1);
		return null;
	}
	
	@Override
	protected int insertData(byte[] newData, byte[] newIndex, long minId, long maxId) {
		// TODO Auto-generated method stub
//		if(minId<this.data.getMinId()){
//			this.data.setMinId(minId);
//		}
//		if(maxId > this.data.getMaxId()){
//			this.data.setMaxId(maxId);
//		}
		int indexStartOffset = this.data.getIndexStartOffset();//index 开始的offset
		int minIndexOffset = findIdOffset(minId);//找到最小id的index offset
		int maxIndexOffset = findIdOffset(maxId);//找到最大id的index offset
		
		
		/*
		 * insert into data
		 */
		int minDataStartOffset = this.data.getInt(minIndexOffset-12);//最小id的data offset
		int maxDataStartOffset = this.data.getInt(maxIndexOffset-12);//最大id的data offset
		//最后一个data结束的位置 = data offset+cson length(4) + length
		int DataEndOffset = this.data.getInt(indexStartOffset)+4+this.data.getInt(this.data.getInt(indexStartOffset));
		int length = DataEndOffset-maxDataStartOffset+1;
		int newMaxDataStartOffset = minDataStartOffset + newData.length;
		//搬移大于maxid的块tempData
		this.data.copyBytes(maxDataStartOffset, length, newMaxDataStartOffset);
		//写入newdata
		this.data.putBytes(newData, minDataStartOffset);

		/*
		 * insert into index
		 */
		//获取要搬移的index数据
		//this.data.copyBytes(indexStartOffset, maxIndexOffset - indexStartOffset, indexStartOffset-newindex.length);
		byte[] tempIndex = this.data.getBytes(indexStartOffset, maxIndexOffset - indexStartOffset);
		//index区数据要更新，offset值重新设置 现在data的长度-原来data的长度
		int addOffset = newData.length-(this.data.getInt(maxIndexOffset-12)-this.data.getInt(minIndexOffset-12));
		tempIndex = updateIndex(tempIndex, addOffset);
		//移动index区数据
		this.data.putBytes(tempIndex, indexStartOffset-(newIndex.length-(maxIndexOffset-minIndexOffset)));
		//copyData(tempIndex, indexStartOffset-(index.length-(maxIndexOffset-minIndexOffset)));
		//写入新的index
		this.data.putBytes(newIndex,minIndexOffset+newIndex.length);
		return 0;
	}
	
	
	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.data.AbstractDataExcerpt#insertDataxxx(byte[], byte[], boolean)
	 */
	@Override
	protected int insertData(byte[] newData, Object[] index, long minId, long maxId) throws DataException{
		// TODO Auto-generated method stub
		if(index.length<2){
			throw new DataException("datas in index array is not enough!!!");
		}
//		if(minId<this.data.getMinId()){
//			this.data.setMinId(minId);
//		}
//		if(maxId > this.data.getMaxId()){
//			this.data.setMaxId(maxId);
//		}
		Object[] newIndex = null;
		int maxIdOffsetIndex = findIdOffset(maxId);
		int minIdOffsetIndex = findIdOffset(minId);
		copyLargerThanMaxIdIndex(maxIdOffsetIndex, minIdOffsetIndex, index.length*6);
			
		copyLargerThanMaxIdData(maxIdOffsetIndex, minIdOffsetIndex, newData.length);
			
		insertData(newData, minIdOffsetIndex);
		newIndex = getNewIndex(index);
		int newIndexStartOffset = minIdOffsetIndex - index.length*6;
		insertNewIndex(newIndex , newIndexStartOffset);
		
		
		return 0;
	}
	//index数组为更新的minid|offset|id|offset|maxid|offset
	protected Object[] getNewIndex(Object[] index) {
		
		long minId = (long) index[0];
		int minIdOffset = findIdOffset(minId); //找到最小id在index区中offset
		int minIdDataOffset = this.data.getInt(minIdOffset-12); //找到最小id的data在data区中的offset
		for(int i = 0; i < index.length; i = i + 2){
			index[i+1] = (int)index[i+1] + minIdDataOffset;
		}
		return index;
	}	
	/**
	 * @param newData
	 * @param minIdOffsetIndex
	 */
	private void insertData(byte[] newData, int minIdOffsetIndex) {
		// TODO Auto-generated method stub
		int minIdOffsetData = this.data.getInt(minIdOffsetIndex-12);
		this.data.putBytes(newData, minIdOffsetData);
		
	}
	/**
	 * @param maxIdOffsetIndex
	 * @param minIdOffsetIndex
	 * @param length
	 */
	private void copyLargerThanMaxIdData(int maxIdOffsetIndex,
			int minIdOffsetIndex, int dataLength) {
		int maxIdOffsetData = this.data.getInt(maxIdOffsetIndex-12);
		int minIdOffsetData = this.data.getInt(minIdOffsetIndex-12);
		int copyLength = this.data.getDataEndOffset() - maxIdOffsetData;
		int lengthBetweenMinAndMax = maxIdOffsetData - minIdOffsetData;
		int desOffset = maxIdOffsetData + (dataLength - lengthBetweenMinAndMax);
		this.data.copyBytes(maxIdOffsetData, copyLength, desOffset);
		// TODO Auto-generated method stub
		
	}
	/**
	 * @param newIndex
	 */
	private void insertNewIndex(Object[] newIndex, int offset) {
		// TODO Auto-generated method stub
		byte[] b = new byte[newIndex.length*6];
		byte[] intbyte = new byte[4];
		byte[] longbyte = new byte[8];
		for(int i = 0; i < newIndex.length/2; i++){
			System.out.println(i);
			try {
				longbyte = getBytes(newIndex[i*2]);
				intbyte = getBytes(newIndex[i*2+1]);
				System.arraycopy(longbyte, 0, b, i*12, 4);
				System.arraycopy(intbyte, 0, b, i*12+4, 8);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.data.putBytes(b, offset);
	}
	private static byte[] getBytes(Object obj) throws IOException {  
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
	 * @param maxId
	 */
	private void copyLargerThanMaxIdIndex(int maxIdOffsetIndex, int minIdOffsetIndex, int indexLength) {
		// TODO Auto-generated method stub
		int startOffset = this.data.getIndexStartOffset();
		int copyLength = maxIdOffsetIndex-startOffset;
		int oldLength = minIdOffsetIndex - maxIdOffsetIndex;
		int desOffset = startOffset - (indexLength - oldLength);
		this.data.copyBytes(startOffset, copyLength, desOffset);
	}


	



	
	

}
