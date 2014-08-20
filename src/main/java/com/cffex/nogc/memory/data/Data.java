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
	private int freesapce;
	private int capacity;
	

	private long minId;
	private long maxId;
	private int count;
	
	protected Data(int capacity){
		this.freesapce = capacity;
		this.capacity = capacity;
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

	protected int getCapacity() {
		return capacity;
	}
	protected void setCapacity(int capacity) {
		this.capacity = capacity;
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
