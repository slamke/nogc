package com.cffex.nogc.memory.data;
/**
 * @author Tao Zhou
 * @ClassName DataExcerpt
 * @Description: Data操作接口
 */
import java.lang.reflect.Array;

import com.cffex.nogc.memory.Segment;
import com.cffex.nogc.memory.SegmentExcerpt;
import com.cffex.nogc.memory.SegmentOperateable;
import com.cffex.nogc.memory.buffer.Buffer;

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
	private int writeData(byte[] b, long position){
		return 0;
	}
	private int copyData(byte[] b, long position){
		return 0;
	}
	private Array GetIndexRegion(long minid, long maxid){
		return null;
	}
	private int setIndexRegion(){
		return 0;
	}
	private int checkFreeSpace(){
		if(data.getFreesapce() < Data.THRESHOLD){
			return 0;
		}else{
			return 1;
		}
	}
	private int binarySearchById(long id){
		
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
		return null;
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
	public byte[] getDataWithIdRange(long minid, long maxid) {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
