package com.cffex.nogc.memory.data;
/**
 * @author Tao Zhou
 * @ClassName DataExcerpt
 * @Description: Data操作接口
 */
import java.lang.reflect.Array;

import com.cffex.nogc.memory.SegmentExcerpt;

public class DataExcerpt implements DataOperateable{

	//private region
	private Data data;
	private SegmentExcerpt segmentExcerpt;
	
	DataExcerpt(){
		
	}
	
	DataExcerpt(SegmentExcerpt se){
		this.segmentExcerpt = se;
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
		return 0;
	}
	private int binarySearchById(){
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
