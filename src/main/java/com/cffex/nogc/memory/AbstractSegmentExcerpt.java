
/**
 *@author  Tao Zhou
*@classname AbstractSegmentExcerpt.java
*@date 上午9:05:19
*@description
 */
package com.cffex.nogc.memory;

import java.nio.ByteBuffer;
import java.util.List;

import com.cffex.nogc.memory.SegmentOperateable;
import com.cffex.nogc.memory.buffer.BufferLog;
import com.cffex.nogc.memory.buffer.BufferLog.BufferLogType;

/**
 * @author zhou
 *
 */
public abstract class AbstractSegmentExcerpt implements SegmentOperateable {

	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.SegmentOperateable#insertItem(long, byte[])
	 */
	@Override
	public boolean insertItem(long id, byte[] value) {
		// TODO Auto-generated method stub
		BufferLog bufferLog = new BufferLog(BufferLogType.INSERT, id, value);

		return addBufferLog(bufferLog);
	}
	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.SegmentOperateable#deleteItem(long)
	 */
	@Override
	public boolean deleteItem(long id) {
		// TODO Auto-generated method stub
		BufferLog bufferLog = new BufferLog(BufferLogType.DELETE, id, null);
		return addBufferLog(bufferLog);
	}
	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.SegmentOperateable#deleteIdRange(long, long)
	 */
	@Override
	public boolean deleteIdRange(long minId, long maxId) {
		for(long id = minId; id <= maxId; id++){
			BufferLog bufferLog = new BufferLog(BufferLogType.DELETE, id, null);
			if(addBufferLog(bufferLog)){
				//do nothing
			}else{
				return false;
			}
		}
		return true;
	}
	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.SegmentOperateable#updateItem(long, byte[])
	 */
	@Override
	public boolean updateItem(long id, byte[] newValue) {
		// TODO Auto-generated method stub
		BufferLog bufferLog = new BufferLog(BufferLogType.UPDATE_ALL, id, newValue);
		return addBufferLog(bufferLog);
	}
	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.SegmentOperateable#updateItemProperty(long, int, byte[])
	 */
	@Override
	public boolean updateItemProperty(long id, int index, byte[] newValue, String schemaKey) {
		// TODO Auto-generated method stub
		BufferLog bufferLog = new BufferLog(BufferLogType.UPDATE_PROPERTY, id, newValue,index,schemaKey);
		return addBufferLog(bufferLog);
	}

	
	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.SegmentOperateable#getItem(long)
	 */
	@Override
	public byte[] getItem(long id,String schemaKey) {
		// TODO Auto-generated method stub
		List<BufferLog> bufferList = getItemInBuffer(id);//Buffer区搜索
		if( bufferList.size() > 0){
			//buffer中数据是否完整，如果不完整，那么需要到data中查找，buffer和data中数据整合得到新的数据
			//不完整的情况下，bufferlog的flag应该都是BufferLogType.UPDATE_PROPERTY
			
			boolean complete = isDataCompleteInBuffer(bufferList);
			if(complete){
				return mergeInBuffer(id, bufferList, schemaKey);	
			}else{
				byte[] dataValue = getItemInData(id);
				return mergeData(id, bufferList, dataValue, schemaKey);
			}
		}else{
			return getItemInData(id);//在Data区搜索
		}
	}



	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.SegmentOperateable#getItemProperty(long, int)
	 */
	@Override
	public byte[] getItemProperty(long id, int index,  String schemaKey) {
		// TODO Auto-generated method stub
		List<BufferLog> bufferList = getItemInBuffer(id);
		if(bufferList.size() > 0){
			boolean complete = isDataPropertyCompleteInBuffer(bufferList, index);
			if(complete){
				return mergeInBuffer(bufferList,index, schemaKey);
			}else{
				byte[] dataValue = getItemPropertyInData(id, index, schemaKey);
				return mergeData(bufferList, dataValue,index);
			}
		}else{
			getItemPropertyInData(id,index, schemaKey);
		}
		return null;
	}
	

	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.SegmentOperateable#freeSegment()
	 */
	@Override
	public boolean freeSegment() {
		// TODO Auto-generated method stub
		release();
		return false;
	}






	
	

	protected abstract boolean addBufferLog(BufferLog log);

	protected abstract void release();
	
	protected abstract List<BufferLog> getItemInBuffer(long id);
	
	protected abstract byte[] getItemInData(long id);
	
	protected abstract byte[] getItemPropertyInData(long id, int index, String schemaKey);
	
	protected abstract boolean isDataCompleteInBuffer(List<BufferLog> bufferList);
	
	protected abstract boolean isDataPropertyCompleteInBuffer(List<BufferLog> bufferList, int index);
		
	protected abstract byte[] mergeInBuffer(long id, List<BufferLog> bufferList, String schemaKey);
	
	protected abstract byte[] mergeInBuffer(List<BufferLog> bufferList, int index, String schemaKey);
	
	protected abstract byte[] mergeData(long id, List<BufferLog> bufferList, byte[] dataValue, String schemaKey);

	protected abstract byte[] mergeData(List<BufferLog> bufferList, byte[] dataValue, int index);
	
}


