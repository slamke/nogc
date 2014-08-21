package com.cffex.nogc.memory.data;
/**
 * @author Tao Zhou
 * @ClassName DataExcerpt
 * @Description: Data操作接口
 */
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import com.cffex.nogc.enumeration.IsolationType;
import com.cffex.nogc.memory.NoGcByteBuffer;
import com.cffex.nogc.memory.Segment;
import com.cffex.nogc.memory.SegmentExcerpt;
import com.cffex.nogc.memory.SegmentOperateable;
import com.cffex.nogc.memory.buffer.Buffer;
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
			int result = data.getInt(offset);
			return result;
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
		            int offset = Data.OFFSET+data.getCapacity()-middle*12-8;
		            if(id == data.getLong(offset)) {   
		                return offset-4;   
		            }else if(id <data.getLong(offset)) {   
		                high = middle - 1;   
		            }else {   
		                low = middle + 1;   
		            }  
		        }  
		        return -1; 
		}

	}
	
	/*
	 * 找到id插入的合适位置
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
			            int offset = this.data.getIndexEndOffset()-middle*12-8;
			            if(id == data.getLong(offset)) {   
			                return offset-4;   
			            }else if(id <data.getLong(offset)) {   
			                high = middle - 1;   
			            }else {   
			                low = middle + 1;   
			            }  
			        }  
			        return this.data.getIndexEndOffset()-low*12;
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
			result[(size-1)*2] = data.getLong(offset1-8);
			result[(size-1)*2+1] = data.getInt(offset1-4);
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
		int size = index.length/12;
		for(int i = 0; i<size; i++){
			int newoffset = data.getInt(size*12)+addoffset;
			index[12*i] = (byte) ((newoffset >> 24) & 0xFF);
			index[12*i+1] = (byte) ((newoffset >> 16) & 0xFF);
			index[12*i+2] = (byte) ((newoffset >> 8)  & 0xFF);
			index[12*i+3] = (byte) (newoffset& 0xFF);
		}
		return null;
		
	}
	private int updateIndexData(byte[] index, int addoffset){
		return 0;
	}
	
	
	

	protected int insertDataWithIdRangexxx(byte[] newData, byte[] newIndex, long minId, long maxId) {
		// TODO Auto-generated method stub
		if(minId<this.data.getMinId()){
			this.data.setMinId(minId);
		}
		if(maxId > this.data.getMaxId()){
			this.data.setMaxId(maxId);
		}
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




	protected byte[] getDataById(long id) {
		// TODO Auto-generated method stub
		int offset = getOffsetById(id);
		if(offset < 0){
			return null;
		}else{
			return this.data.getBytes(offset);
		}
	}




	protected byte[] getDataWithIdRangexxx(long minId, long maxId) {
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


	
	

}
