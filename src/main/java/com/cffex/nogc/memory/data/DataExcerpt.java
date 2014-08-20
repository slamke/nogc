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

import com.cffex.nogc.memory.Segment;
import com.cffex.nogc.memory.SegmentExcerpt;
import com.cffex.nogc.memory.SegmentOperateable;
import com.cffex.nogc.memory.buffer.Buffer;
import com.cffex.nogc.memory.utils.MemoryTool;
import com.sun.beans.editors.ByteEditor;

public class DataExcerpt implements DataOperateable{

	//private region
	private Data data;
	

	private SegmentExcerpt segmentExcerpt;
	
	public DataExcerpt(){
		
	}
	
	public DataExcerpt(SegmentExcerpt segmentExcerpt){
		this.segmentExcerpt = segmentExcerpt;
		data = new Data(Segment.DEFAULT_CAPACITY-Buffer.CAPACITY);
	}
	public Data getData() {
		return data;
	}
	//private methods to implement functions in DataOperateable
	
	private int getIndexStartOffset(){
		return Data.OFFSET+data.getCapacity()-data.getCount()*12;
	}
	@SuppressWarnings("unused")
	private int writeData(byte[] b, int offset){
		MemoryTool mt = new MemoryTool();
		mt.writeBytes(b,segmentExcerpt.getPositonByOffset(offset));
		return 1;
	}
	private int copyData( int offset0, int length, int offset1){
		MemoryTool mt = new MemoryTool();
		mt.copyBytes(segmentExcerpt.getPositonByOffset(offset0), length, segmentExcerpt.getPositonByOffset(offset1));
		return 1;
	}
	private int copyData(byte[] b, int offset){
		MemoryTool mt = new MemoryTool();
		mt.copyBytes(b,segmentExcerpt.getPositonByOffset(offset));
		return 1;
	}
	
	//read long
	private long getLong(int offset){
		MemoryTool mt = new MemoryTool();
		long l = mt.getLong(segmentExcerpt.getPositonByOffset(offset));
  		return l;
	}
	
	//read int
	private int getInt(int offset){
		MemoryTool mt = new MemoryTool();
		int i = mt.getInt(segmentExcerpt.getPositonByOffset(offset));
  		return i;
	}
	
	/*
	 * @param offset data的offset
	 * 不同的存储形式 长度存放在哪
	 * 
	 */
	private byte[] getBytes(int offset){
		int length = getInt(offset); //CSON前四位为长度
		byte[] b = new byte[length+4]; //加上len本身4byte
  		for(int i=0;i<length;i++){  
  			MemoryTool mt = new MemoryTool();
  			b[i]=mt.getByte(segmentExcerpt.getPositonByOffset(i+offset));
  		}
		return b;
	}
	
	private byte[] getBytes(int offset, int length){
		byte[] b = new byte[length];
		for(int i=0;i<length;i++){  
  			MemoryTool mt = new MemoryTool();
  			b[i]=mt.getByte(segmentExcerpt.getPositonByOffset(i+offset));
  		}
		return b;
	}
	

	
	/*
	 * 在Index区二分查找
	 * @param id 查找的id
	 * @return offset的值
	 */
	private int getOffsetById(long id) {
		int offset = binarySearchById(id);
		if(offset < 0){
			return -1;
		}else{
			int result = getInt(offset);
			return result;
		}
		
	}
	
	/*
	 * index(id+offset)
	 * 找到index中id对应offset的位置
	 */
	private int binarySearchById(long id){
		
		if(data.getCount() == 0|| id < data.getMinId() || id > data.getMaxId()){
			return -1;
		}else{
			int low = 0;   
		        int high = data.getCount()-1; 
		        while(low <= high) {   
		            int middle = (low + high)/2;   
		            int offset = Data.OFFSET+data.getCapacity()-middle*12-8;
		            if(id == getLong(offset)) {   
		                return offset-4;   
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
	 * 找到id的合适位置
	 */
	private int findIdOffset(long id){
		if(data.getCount() == 0){
			return Data.OFFSET+data.getCapacity();
		}else{
			if(id > data.getMaxId()){
				return Data.OFFSET+data.getCapacity()-data.getCount()*12;
			}else if(id< data.getMinId()){
				return Data.OFFSET+data.getCapacity();
			}else{
				int low = 0;   
			        int high = data.getCount()-1; 
			        while(low <= high) {   
			            int middle = (low + high)/2;   
			            int offset = Data.OFFSET+data.getCapacity()-middle*12-8;
			            if(id == getLong(offset)) {   
			                return offset-4;   
			            }else if(id <getLong(offset)) {   
			                high = middle - 1;   
			            }else {   
			                low = middle + 1;   
			            }  
			        }  
			        return Data.OFFSET+data.getCapacity()-low*12;  //若没有找到  
			}
		}
	}

	
	private Object[] GetIndexRegion(long minid, long maxid){
		
		int offset1 = findIdOffset(minid);
		int offset2 = findIdOffset(maxid);
		int size = (offset1-offset2)/12;
		Object[] result = new Object[size*2];
		for(int i = 0; i < size; i++){
			result[(size-1)*2] = getLong(offset1-8);
			result[(size-1)*2+1] = getInt(offset1-8);
		}
		return result;
	}
	private int setIndexRegion(){
		return 0;
	}
	@SuppressWarnings("unused")
	private int checkFreeSpace(){
		if(data.getFreesapce() < Data.THRESHOLD){
			return 0;
		}else{
			return 1;
		}
	}
	
	private int resize(){
		return 0;
	}
	
	//更新index中的offset值
	private byte[] updateIndex(byte[] index, int addoffset){
		int size = index.length/12;
		for(int i = 0; i<size; i++){
			int newoffset = getInt(size*12)+addoffset;
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
	
	
	
	//implements DataOperateable
	@Override
	public int insertDataWithIdRange(byte[] data, byte[] index, long minId, long maxId) {
		// TODO Auto-generated method stub
		if(minId<getData().getMinId()){
			getData().setMinId(minId);
		}
		if(maxId > getData().getMaxId()){
			getData().setMaxId(maxId);
		}
		int indexStartOffset = getIndexStartOffset();//index 开始的offset
		int minIndexOffset = findIdOffset(minId);//找到最小id的index offset
		int maxIndexOffset = findIdOffset(maxId);//找到最大id的index offset

		/*
		 * insert into data
		 */
		int minDataStartOffset = getInt(minIndexOffset-12);//最小id的data offset
		int maxDataStartOffset = getInt(maxIndexOffset-12);//最大id的data offset
		//最后一个data结束的位置 = data offset+cson length(4) + length
		int DataEndOffset = getInt(indexStartOffset)+4+getInt(getInt(indexStartOffset));
		byte[] tempdata = getBytes(maxDataStartOffset, DataEndOffset-maxDataStartOffset+1);
		copyData(data, minDataStartOffset);
		copyData(tempdata, minDataStartOffset+data.length);
		/*
		 * insert into index
		 */
		//获取要搬移的index数据
		byte[] tempIndex = getBytes(indexStartOffset, maxIndexOffset - indexStartOffset);
		//index区数据要更新，offset值重新设置 现在data的长度-原来data的长度
		int addOffset = data.length-(getInt(maxIndexOffset-12)-getInt(minIndexOffset-12));
		tempIndex = updateIndex(tempIndex, addOffset);
		//移动index区数据
		copyData(tempIndex, indexStartOffset-(index.length-(maxIndexOffset-minIndexOffset)));
		//写入新的index
		writeData(index, minIndexOffset+index.length);
		return 0;
	}

	@Override
	public int tryReadLock() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte[] getById(long id) {
		// TODO Auto-generated method stub
		int offset = getOffsetById(id);
		if(offset < 0){
			return null;
		}else{
			return getBytes(offset);
		}
	}

	@Override
	public int tryWriteLock() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int unlockWriteLock() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte[] getDataWithIdRange(long minId, long maxId) {
		// TODO Auto-generated method stub
		int minIndexOffset = findIdOffset(minId);//找到最小id的index offset
		int maxIndexOffset = findIdOffset(maxId);//找到最大id的index offset
		int minDataOffset = getInt(minIndexOffset-12);//最小id的data offset
		int maxDataOffset = getInt(maxIndexOffset-12);//最大id的data offset
		//最后一个data结束的位置 = data offset+cson length(4) + length
		int maxDataEndOffset = getInt(maxDataOffset) + 4 +getInt(getInt(maxDataOffset));
		byte[] result = getBytes(minDataOffset, maxDataEndOffset - minDataOffset+1);
		return result;
	}
	
	

}
