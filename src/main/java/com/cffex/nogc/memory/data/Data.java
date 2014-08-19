package com.cffex.nogc.memory.data;
/**
 * @author Tao Zhou
 * @ClassName Data
 * @Description: Data数据
 */
public class Data {
	
	//阈值设置为256K
	public final static float THRESHOLD = 256;
	
	//buffer 大小为128K
	private static int OFFSET  = 131072;
	private long freesapce;
	private long minId;
	private long maxId;
	private int count;
	
	protected Data(long freespace){
		this.freesapce = freespace;
		this.maxId = 0;
		this.minId = 0;
		this.count = 0;
	}
	protected int updateMetaData(){
		return 0;
	}
	
	protected int getOffserByLength(){
		return 0;
	}
	
	protected int getIndexRegionOffset(){
		return 0;
	}

	protected long getFreesapce() {
		return freesapce;
	}

	protected void setFreesapce(long freesapce) {
		//this.freesapce = freesapce;
	}

	protected long getMinId() {
		return minId;
	}

	protected void setMinId(long minId) {
		this.minId = minId;
	}

	protected void setMaxId(long maxId) {
		this.maxId = maxId;
	}

	protected void setCount(int count) {
		this.count = count;
	}
}
