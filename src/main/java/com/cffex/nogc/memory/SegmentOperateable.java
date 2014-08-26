package com.cffex.nogc.memory;
/**
 * @author sunke
 * @ClassName SegmentExcerpt
 * @Description: segment操作接口
 */
public interface SegmentOperateable {

	/**
	 * 向段segment中插入一条记录
	 * @param id 记录的id
	 * @param value 记录的二进制数据
	 * @return 是否插入成功的结果标识
	 */
	public boolean insertItem(long id,byte[] value);
	
	/**
	 * 删除段segment中id对应的记录
	 * @param id 待删除记录的id
	 * @return 是否成功删除的结果标识
	 */
	public boolean deleteItem(long id);
	
	/**
	 * 获取id对应的记录的所有数据
	 * @param id 待获取数据记录的id
	 * @return 数据
	 */
	public byte[] getItem(long id, String schemaKey);
	
	/**
	 * 获取id对应的记录第index条property的数据
	 * @param id 待获取数据记录的id
	 * @param index 待获取property的索引
	 * @return	property的数据(typecode + data) 
	 */
	public byte[] getItemProperty(long id ,int index, String schemaKey);
	/**
	 * 更新段segment中的id对应的记录
	 * @param id 记录的id
	 * @param newValue 记录的二进制新值
	 * @return 是否成功更新的结果标识
	 */
	public boolean updateItem(long id, byte[] newValue);
	
	/**
	 * 更新段segment中的id对应的记录的第index个property
	 * @param id 记录的id
	 * @param index 待更新property的索引
	 * @param newValue 记录的二进制新值
	 * @return 是否成功更新的结果标识
	 */
	public boolean updateItemProperty(long id ,int index ,byte[] newValue, String schemaKey);
	
	/**
	 * 当段segment中没有记录时，释放整个段的内存
	 * @return  是否成功释放空间的结果标识
	 */
	public boolean freeSegment();
	
	/**
	 * 删除段segment中id范围在minId和maxId之间对应的记录(包括minId和maxId)
	 * @param minId 待删除记录范围的最小id
	 * @param maxId 待删除记录范围的最大id
	 * @return 是否删除成功的结果标识
	 */
	public boolean deleteIdRange(long minId, long maxId);
}
