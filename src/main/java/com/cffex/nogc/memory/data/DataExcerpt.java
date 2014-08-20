package com.cffex.nogc.memory.data;
/**
 * @author Tao Zhou
 * @ClassName DataExcerpt
 * @Description: Data操作接口
 */
import java.lang.reflect.Array;
import java.util.Arrays;

import com.cffex.nogc.memory.Segment;
import com.cffex.nogc.memory.SegmentExcerpt;
import com.cffex.nogc.memory.SegmentOperateable;
import com.cffex.nogc.memory.buffer.Buffer;
import com.cffex.nogc.memory.utils.MemoryTool;

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
	//private methods to implement functions in DataOperateable
	@SuppressWarnings("unused")
	private int writeData(byte[] b, int position){
		MemoryTool mt = new MemoryTool();
		mt.writeBytes(b,position);
		return 1;
	}
	private int copyData( int position0, int length, int position1){
		MemoryTool mt = new MemoryTool();
		mt.copyBytes(position0, length, position1);
		return 1;
	}
	private Array GetIndexRegion(long minid, long maxid){
		return null;
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
	
	//read long
	private long getLong(long position){
		MemoryTool mt = new MemoryTool();
		long l = mt.getLong(position);
  		return l;
	}
	
	//read int
	private int getInt(int position){
		MemoryTool mt = new MemoryTool();
		int i = mt.getInt(position);
  		return i;
	}
	private byte[] getBytes(int offset){
		int len = getInt(offset);
		byte[] b = new byte[len];
  		for(int i=0;i<len;i++){  
  			MemoryTool mt = new MemoryTool();
  			b[i]=mt.getByte(i+offset); //存储位置在foo之后
  		}
		return b;
	}
	
	private int binarySearch(int begin, int end, long id){
		int position = Data.OFFSET+data.getCapacity()-(begin+end)/2-8;
		long minId = getLong(position);
		if(end-begin>=8){
			if(id>minId){
				binarySearch(begin,position+8,id);
			}else if(id<minId){
				binarySearch(position-4,end,id);
			}else{
				return position-4;
			}
		}else{
			return -1;
		}
		return -1;
	}
	
	/*
	 * 在Index区二分查找
	 * @para id 查找的id
	 * @return offset
	 */
	private int getOffsetById(long id) {
		int position = binarySearchById(id);
		if(position < 0){
			return -1;
		}else{
			int offset = getInt(position);
			return offset;
		}
		
	}
	
	private int binarySearchById(long id){
		int begin = 0;
		int end = data.getCount()*12;//index为int+long
		int position = binarySearch(begin, end, id);
		return position;
	}
	
	private int resize(){
		return 0;
	}
	private int updateIndexData(){
		return 0;
	}
	
	//implements DataOperateable
	@Override
	public int insertDataWithIdRange(byte[] b, long minid, long maxid) {
		// TODO Auto-generated method stub
		
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

	public int test(){
		return 0;
	}
	
	@Override
	public int unlockWriteLock() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte[] getDataWithIdRange(long minid, long maxid) {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
