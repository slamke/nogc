package com.cffex.nogc.memory.data;

import java.util.List;

import com.cffex.nogc.memory.SegmentExcerpt;

/**
 * @author Tao Zhou
 * @ClassName DataOperateable
 * @Description: Data接口
 */
public interface DataOperateable {
	/**
	 * @param callingThread
	 * @return
	 */
	public boolean tryReadLock();
	/**
	 * @param callingThread
	 * @return
	 */
	public void unLockReadLock();
	/**
	 * @param callingThread
	 * @return
	 */
	void tryWriteLock();
	/**
	 * @param callingThread
	 * @return
	 */
	public void unLockWriteLock();
	/**
	 * @param id 为待查找对象的id
	 * @return 返回值为待查找对象的byte[]，若不存在返回null;
	 */
	public byte[] getById(long id);
	
	/**
	 * @param id 为待查找对象的id
	 * @param index 对象的第几个属性
	 * @return 返回值为待查找对象的byte[]，若不存在返回null;
	 */
	public byte[] getPropertyById(long id, int index, String schemaKey);
	/**
	 * @param minId 最小id
	 * @param maxId 最大id
	 * @return 得到id在minId和maxId之间的data数组
	 */
	public byte[] getDataWithIdRange0(long minId, long maxId);
	
	/**
	 * @param minId 最小id
	 * @param maxId 最大id
	 * @return 得到id在minId和maxId之间的index，第一个indexoffset设置为0；
	 */
	public List<IndexItem> getIndexRegion(long minId, long maxId);
	/**
	 * @param minId 最小id
	 * @param maxId 最大id
	 * @return 得到id在minId和maxId之间的BlockData,由indexItem list和包含data数据的buffer组成
	 */
	public BlockData getDataWithIdRange(long minId, long maxId);

	
	/**
	 * @param blockData  插入数据，list中为(id,offset)，id从小到大，offset从0开始，buffer中为data数据
	 * @param minId 插入的最小id
	 * @param maxId 插入的最大id
	 */
	public int insertDataWithIdRange(BlockData blockData, long minId,long maxId);
	
	/**
	 * @param dataBytes 插入数据的byte数组
	 * @param indexItems 插入的IndexItem数组(id+offset), id从小到大，offset从0开始
	 * @param minId 插入的最小id
	 * @param maxId 插入的最大id
	 */
	public void insertDataWithIdRange(byte[] dataBytes, IndexItem[] indexItems, long minId, long maxId);

	
	/**
	 * @param dataBytes 插入数据的byte数组
	 * @param indexBytes 插入的index的byte数组(id+offset), id从小到大，offset从0开始
	 * @param minId 插入的最小id
	 * @param maxId 插入的最大id
	 */
	
	public void insertDataWithIdRange(byte[] dataBytes, byte[] indexBytes, long minId, long maxId);
	
	/**
	 * @param dataBytes  插入数据的byte数组
	 * @param indexBytes 插入的index byte数组(id+offset),id从小到大，offset从0开始
	 * @param minId 插入的最小id
	 * @param maxId 插入的最大id
	 * @param readonly 读的级别
	 */
	public void insertData(BlockData blockData, long minId, long maxId, boolean readonly);
	
	/**
	 * @param dataBytes 插入数据的byte数组
	 * @param indexItems 插入的IndexItem数组(id+offset),id从小到大，offset从0开始
	 * @param minId 插入的最小id
	 * @param maxId 插入的最大id
	 * @param readonly 读的级别
	 */
	public void insertData(byte[] dataBytes, IndexItem[] newIndex, long miId, long maxId, boolean readonly);
	
	/**
	 * @param dataBytes  插入数据的byte数组
	 * @param indexBytes 插入的index byte数组(id+offset),id从小到大，offset从0开始
	 * @param minId 插入的最小id
	 * @param maxId 插入的最大id
	 * @param readonly 读的级别
	 */
	public void insertData(byte[] dataBytes, byte[] indexBytes, long minId, long maxId, boolean readonly);

	
	
	
	
}
