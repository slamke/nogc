/**
 *@author  Tao Zhou
*@classname AbstractSegmentExcerpt.java
*@date 上午9:05:19
*@description
 */
package com.cffex.nogc.memory;

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
	 * @see com.cffex.nogc.memory.SegmentOperateable#getItem(long)
	 */
	@Override
	public byte[] getItem(long id) {
		// TODO Auto-generated method stub
		List<BufferLog> bufferList = findItemInBuffer(id);//Buffer区搜索
		if( bufferList.size() > 0){
			boolean complete = isDataCompleteInBuffer(bufferList);
			byte[] bufferValue = null;
			if(complete){
				
				return bufferValue;
			}else{
				byte[] dataValue = findItemInData(id);
				return mergeData(bufferValue, dataValue);
			}
		}else{
			return findItemInData(id);//在Data区搜索
		}
	}











	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.SegmentOperateable#getItemProperty(long, int)
	 */
	@Override
	public byte[] getItemProperty(long id, int index) {
		// TODO Auto-generated method stub
		List<BufferLog> bufferList = findItemInBuffer(id);
		if(bufferList.size() > 0){
			boolean complete = isDataPropertyCompleteInBuffer(bufferList, index);
		}else{
			findItemPropertyInData(id,index);
		}
		return null;
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
	public boolean updateItemProperty(long id, int index, byte[] newValue) {
		// TODO Auto-generated method stub
		BufferLog bufferLog = new BufferLog(BufferLogType.UPDATE_PROPERTY, id, newValue,index);
		return addBufferLog(bufferLog);
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
	
	
	/**
	 * @param log
	 */
	protected abstract boolean addBufferLog(BufferLog log);
	/**
	 * 
	 */
	protected abstract void release();
	

	protected abstract List<BufferLog> findItemInBuffer(long id);

	protected abstract byte[] findItemInData(long id);
	
	protected abstract byte[] mergeData(byte[] bufferValue, byte[] dataValue);
	
	protected abstract boolean isDataCompleteInBuffer(List<BufferLog> bufferList);
	
	protected abstract boolean isDataPropertyCompleteInBuffer(List<BufferLog> bufferList, int index);
	
	protected abstract byte[] findItemPropertyInData(long id, int index);
}
