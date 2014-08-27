package com.cffex.nogc.memory;

import java.util.Iterator;
import java.util.List;

import sun.nio.ch.DirectBuffer;

import com.cffex.nogc.cson.core.utils.CSONMergeTool;
import com.cffex.nogc.cson.core.utils.Tuple;
import com.cffex.nogc.enumeration.IsolationType;
import com.cffex.nogc.memory.buffer.Buffer;
import com.cffex.nogc.memory.buffer.BufferExcerpt;
import com.cffex.nogc.memory.buffer.BufferLog;
import com.cffex.nogc.memory.buffer.BufferOperatable;
import com.cffex.nogc.memory.buffer.BufferLog.BufferLogType;
import com.cffex.nogc.memory.data.Data;
import com.cffex.nogc.memory.data.DataExcerpt;
import com.cffex.nogc.memory.data.DataItem;
import com.cffex.nogc.memory.data.DataOperateable;
import com.cffex.nogc.memory.utils.MemoryTool;

/**
 * SegmentExcerpt
 * @author sunke TaoZhou
 * @ClassName SegmentExcerpt
 * @Description: SegmentExcerpt实现了SegmentOperateable接口，提供了段操作的真实实现
 */
public class SegmentExcerpt extends AbstractSegmentExcerpt {
	/**
	 * 隔离级别 
	 */
	private IsolationType isolationType;
	
	/**
	 * 段数据
	 */
	private Segment segment;

	


	private BufferOperatable bufferOperatable;
	
	private DataOperateable dataOperateable;
	
	/**
	 * @param isolationType 隔离级别 
	 * @param segment 段数据
	 */
	public SegmentExcerpt(IsolationType isolationType) {
		super();
		DirectBuffer directBuffer = MemoryTool.allocate(Segment.DEFAULT_CAPACITY);
		segment = new Segment(directBuffer);
		NoGcByteBuffer nogcData = new NoGcByteBuffer(Data.OFFSET, Segment.DEFAULT_CAPACITY - Buffer.CAPACITY,
				directBuffer);
		NoGcByteBuffer nogcBuffer = new NoGcByteBuffer(Buffer.OFFSET, Buffer.CAPACITY,
				directBuffer);
		this.isolationType = isolationType;
		this.dataOperateable = new DataExcerpt(this,nogcData);
		this.bufferOperatable = new BufferExcerpt(this,nogcBuffer);
	}
	
	public SegmentExcerpt(IsolationType isolationType, Segment segment, NoGcByteBuffer nogcData, NoGcByteBuffer nogcBuffer) {
		super();

		this.segment = segment;
		this.isolationType = isolationType;
		this.dataOperateable = new DataExcerpt(this,nogcData);
		this.bufferOperatable = new BufferExcerpt(this,nogcBuffer);
	}
	
	public SegmentExcerpt(IsolationType isolationType, Segment segment, Data data, Buffer buffer) {
		super();

		this.segment = segment;
		this.isolationType = isolationType;
		this.dataOperateable = new DataExcerpt(this,data);
		this.bufferOperatable = new BufferExcerpt(this,buffer.getNoGcByteBuffer());
	}
	
	public Segment getSegment() {
		return segment;
	}

	public IsolationType getIsolationType() {
		return isolationType;
	}
	/**
	 * 将buffer区获取的operation与data区中的数据进行merge
	 * @return merge后的结果
	 */
	private byte[] mergeDataWithOperation(){
		return null;
	}

	public void setReadonly(boolean readonly){
		segment.setReadonly(readonly);
	}
	public boolean isReadonly() {
		return segment.isReadonly();
	}
	public DataOperateable getDataOperateable(){
		return this.dataOperateable;
	}
	public BufferOperatable getBufferOperateable(){
		return this.bufferOperatable;
	}
	/**
	 * TODO MERGE时，根据隔离级别设置lock
	 */
	public void mergeCallback(){}
	
	protected boolean addBufferLog(BufferLog bufferLog){
		return this.bufferOperatable.appendOperation(bufferLog);
	}
	protected void release(){
		this.getSegment().release();
	}
	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.AbstractSegmentExcerpt# isDataCompleteInBuffer(long)
	 */
	@Override
	protected boolean isDataCompleteInBuffer(List<BufferLog> bufferList){
//		INSERT(0),
//		DELETE(1),
//		UPDATE_ALL(2),
//		UPDATE_PROPERTY(3);
//		如果有DALETE、INSERT、UPDSTE_ALL那么buffer中的数据时完整的
		for(BufferLog bLog : bufferList){
			if(bLog.getFlag().equals(BufferLogType.DELETE)||
					bLog.getFlag().equals(BufferLogType.INSERT)||
					bLog.getFlag().equals(BufferLogType.UPDATE_ALL)){
				return true;
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.AbstractSegmentExcerpt#isDataPropertyCompleteInBuffer(java.util.List)
	 */
	@Override
	protected boolean isDataPropertyCompleteInBuffer(
			List<BufferLog> bufferList, int index) {
		// TODO Auto-generated method stub
		//如果有对这个id进行过
		//DELETE操作，INSERT操作，UPDATE_ALL操作
		//或者是对这个id的这个index的值做过更新即UPDATE_PROPERTY操作，那么buffer中的值是最新的
		for(BufferLog bLog : bufferList){
			if(bLog.getFlag().equals(BufferLogType.DELETE)||
					bLog.getFlag().equals(BufferLogType.INSERT)||
					bLog.getFlag().equals(BufferLogType.UPDATE_ALL)||
					(bLog.getFlag().equals(BufferLogType.UPDATE_PROPERTY)&&bLog.getIndex() == index)){
				return true;
			}
		}
		return false;
	}
	

	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.AbstractSegmentExcerpt#findItemInBuffer(long)
	 */
	@Override
	protected List<BufferLog> getItemInBuffer(long id) {
		// TODO Auto-generated method stub
		return this.bufferOperatable.getById(id);
	}

	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.AbstractSegmentExcerpt#getItemInData(long)
	 */
	@Override
	protected byte[] getItemInData(long id) {
		// TODO Auto-generated method stub
		return this.dataOperateable.getById(id);
	}
	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.AbstractSegmentExcerpt#findItemPropertyInData(long, int)
	 */
	@Override
	protected byte[] getItemPropertyInData(long id, int index, String schemaKey) {
		// TODO Auto-generated method stub
		return this.dataOperateable.getPropertyById(id, index, schemaKey);
	}



	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.AbstractSegmentExcerpt#mergeInBuffer(java.util.List)
	 */
	@Override
	protected byte[] mergeInBuffer(long id, List<BufferLog> bufferList, String schemaKey) {
		// TODO Auto-generated method stub
		int i;
		byte[] result = null;
		for(i =  bufferList.size() - 1; i >= 0; i--){
			if(bufferList.get(i).getFlag().equals(BufferLogType.DELETE)){
				return null;
			}else if(bufferList.get(i).getFlag().equals(BufferLogType.UPDATE_ALL)){
				result = bufferList.get(i).getValue();
				break;
			}else if(bufferList.get(i).getFlag().equals(BufferLogType.INSERT)){
				result = bufferList.get(i).getValue();
				break;
			}
		}
		if(i < bufferList.size()){
			DataItem dataItem = new DataItem(id, result, schemaKey);
			for(int j = i + 1; j < bufferList.size() - 1; j++){
				dataItem = mergeBufferlog(id, dataItem, bufferList.get(j), schemaKey);
			}
			return dataItem.getValue();
		}else{
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.AbstractSegmentExcerpt#mergeData(byte[], byte[])
	 */
	@Override
	protected byte[] mergeData(long id, List<BufferLog> bufferList, byte[] dataValue,String schemaKey) {
		// TODO Auto-generated method stub
		//不完整的情况下，bufferlog的flag应该都是BufferLogType.UPDATE_PROPERTY
		DataItem dataItem = new DataItem(id, dataValue, schemaKey);
		for(int i = 0; i<bufferList.size() - 1; i++){
			dataItem = mergeBufferlog(id, dataItem,bufferList.get(i),schemaKey);
		}
		return dataItem.getValue();
	}
	private DataItem mergeBufferlog(long id, DataItem dataItem, BufferLog bufferLog,String schemaKey){
		//只会再出现UPDATE_PROPERTY操作
		int index= bufferLog.getIndex();//找到需要更新的index
		byte[] value = bufferLog.getValue();//找到需要更新的value
			//todo：在bufferBytes中更新index值为value
		Tuple updateOperation = new Tuple(index,value);
		try {
			CSONMergeTool.merge(Class.forName(schemaKey), dataItem.toBytebuffer(), updateOperation);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataItem;
		
	}

	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.AbstractSegmentExcerpt#mergeInBuffer(java.util.List, int)
	 */
	@Override
	protected byte[] mergeInBuffer(List<BufferLog> bufferList, int index, String schemaKey) {
		// TODO Auto-generated method stub
		int i;
		byte[] result = null;
		for(i =  bufferList.size() - 1; i >= 0; i--){
			if(bufferList.get(i).getFlag().equals(BufferLogType.DELETE)){
				break;
			}else if(bufferList.get(i).getFlag().equals(BufferLogType.UPDATE_ALL)){
				result = bufferList.get(i).getValue(index);
				break;
			}else if(bufferList.get(i).getFlag().equals(BufferLogType.INSERT)){
				result = bufferList.get(i).getValue(index);
				break;
			}else if(bufferList.get(i).getFlag().equals(BufferLogType.UPDATE_PROPERTY)&&bufferList.get(i).getIndex()==index){
				result = bufferList.get(i).getValue();
				break;
			}
		}
		return result;
	}
	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.AbstractSegmentExcerpt#mergeData(java.util.List, byte[], int)
	 */
	@Override
	protected byte[] mergeData(List<BufferLog> bufferList,
			byte[] dataValue, int index) {
		// TODO Auto-generated method stub
		//不完整的情况下，bufferlog的flag应该都是BufferLogType.UPDATE_PROPERTY
		for(int i = 0; i<bufferList.size() - 1; i++){
			dataValue = mergeBufferlog(dataValue,bufferList.get(i),index);
		}
		return dataValue;
	}
	/**
	 * @param result
	 * @param bufferLog
	 * @param index
	 * @return
	 */
	private byte[] mergeBufferlog(byte[] bufferBytes, BufferLog bufferLog,
			int index) {
		// TODO Auto-generated method stub
		if(bufferLog.getFlag().equals(BufferLogType.DELETE)){
			return null;
		}else{
			if(bufferLog.getFlag().equals(BufferLogType.UPDATE_PROPERTY)&&bufferLog.getIndex()!=index){
				return bufferBytes;
			}
			byte[] result = bufferLog.getValue(index);
			if(result == null){
				return bufferBytes;
			}else{
				return result;
			}
		}
	}




	
}
