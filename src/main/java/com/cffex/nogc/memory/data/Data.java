package com.cffex.nogc.memory.data;
/**
 * @author Tao Zhou
 * @ClassName Data
 * @Description: Data数据
 */
public class Data {
	public final static float REFACTOR = 2;
	public final static float THRESHOLD = 0.95f;
	private static int OFFSET ;
	private long freesapce;
	private long minId;
	private long maxId;

	protected int updateMetaData(){
		return 0;
	}
	
	protected int getOffserByLength(){
		return 0;
	}
	
	protected int getIndexRegionOffset(){
		return 0;
	}
}
