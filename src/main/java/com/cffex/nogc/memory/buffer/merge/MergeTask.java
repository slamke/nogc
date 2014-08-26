/**
 * @time 2014年8月21日
 * @author sunke
 */
package com.cffex.nogc.memory.buffer.merge;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cffex.nogc.cson.core.utils.CSONHelper;
import com.cffex.nogc.cson.core.utils.CSONMergeTool;
import com.cffex.nogc.cson.core.utils.Tuple;
import com.cffex.nogc.memory.SegmentExcerpt;
import com.cffex.nogc.memory.buffer.Buffer;
import com.cffex.nogc.memory.buffer.BufferLog;
import com.cffex.nogc.memory.buffer.BufferLog.BufferLogType;
import com.cffex.nogc.memory.buffer.exception.IllegalBufferMergeException;
import com.cffex.nogc.memory.data.BlockData;
import com.cffex.nogc.memory.data.IndexItem;

/**
 * @author sunke
 * @ClassName MergeTask
 * @Description: MergeTask为不可变的Pojo-->原因：MergeTask作为变量在actor模型框架akka中进行传递，actor要求传递的msg为不可变的 
 */
public class MergeTask {
	private final TempBuffer tempBuffer;
	private final SegmentExcerpt segmentExcerpt;
	/**
	 * @param tempBuffer
	 * @param segmentExcerpt
	 */
	public MergeTask(TempBuffer tempBuffer, SegmentExcerpt segmentExcerpt) {
		super();
		this.tempBuffer = tempBuffer;
		this.segmentExcerpt = segmentExcerpt;
		
	}
	/**
	 * 将merge完成的数据回写至data区中-->merge的最后一步
	 * 
	 * @param blockData
	 */
	private void writeBack(BlockData blockData){
		this.segmentExcerpt.getDataOperateable().insertDataWithIdRange(blockData, this.tempBuffer.getMinId(), this.tempBuffer.getMaxId());
	}
	
	/**
	 * 使用归并排序，进行merge
	 * @param blockData
	 * @return
	 */
	private BlockData mergeOperation(BlockData blockData){
		int len = blockData.getDataBuffer().limit() + Buffer.CAPACITY;
    	//新数据
    	ByteBuffer newDataBuffer =  ByteBuffer.allocate(len).order(ByteOrder.LITTLE_ENDIAN);
    	//新的offsetlist
    	List<IndexItem> newOffsetList = new ArrayList<IndexItem>();
    	
    	//尚未merge的insert操作
    	List<BufferLog> insertLogs = tempBuffer.getInsertIndexList();
    	//尚未merge的update操作
    	List<BufferLog> updateLogs = tempBuffer.getUpdateIndexList();
    	
    	//原有的offset List
    	List<IndexItem> indexItems = blockData.getOffsetList();
    	//原有的数据
    	ByteBuffer oldDataBuffer = blockData.getDataBuffer();

    	//构建一个map，便于merge时，寻找更新操作
    	Map<Long,List<BufferLog>> updateMap  = new HashMap<Long,List<BufferLog>>();
    	if (updateLogs != null && updateLogs.size() >0) {
			for (BufferLog bufferLog : updateLogs) {
				if (updateMap.containsKey(bufferLog.getId())) {
					updateMap.get(bufferLog.getId()).add(bufferLog);
				}else {
					List<BufferLog> list = new ArrayList<BufferLog>();
					list.add(bufferLog);
					updateMap.put(bufferLog.getId(), list);
				}
			}
		}
    	//merge insert操作的同时，merge update操作-->归并排序的思路
    	int i = 0;
    	for (BufferLog log : insertLogs) {
    		//log.getId() > indexItems.get(i).getId()时，处理blockdata中的数据
			while (log.getId() > indexItems.get(i).getId()) {
				//原有数据
				byte[] value = CSONHelper.getCSONFromByteBuffer(oldDataBuffer);
				//有update操作，进行merge
				if (updateMap.containsKey(indexItems.get(i).getId())) {
					List<BufferLog> updateForCurrent = updateMap.get(indexItems.get(i));
					//merge 对应的upate操作
					for (int j = 0; j < updateForCurrent.size(); j++) {
						BufferLog currentUpdate = updateForCurrent.get(j);
						//全部更新，则进行替换
						if (currentUpdate.getFlag() == BufferLogType.UPDATE_ALL) {
							value = currentUpdate.getValue();
						}//部分更新，则进行property的更改
						else if (currentUpdate.getFlag() == BufferLogType.UPDATE_PROPERTY) {
							ByteBuffer cur = ByteBuffer.wrap(value).order(ByteOrder.LITTLE_ENDIAN);
							ByteBuffer mergeBuffer = CSONMergeTool.merge(currentUpdate.getClass(), cur, new Tuple<Integer, byte[]>(currentUpdate.getIndex(), currentUpdate.getValue()));
							value = new byte[mergeBuffer.limit()];
							mergeBuffer.get(value);
						}//删除时，直接标记为null
						else if (currentUpdate.getFlag() == BufferLogType.DELETE) {
							value = null;
						}
					}
					//value为空表示对应的记录被删除
					if (value != null) {
						newOffsetList.add(new IndexItem(indexItems.get(i).getId(), newDataBuffer.position()));
						newDataBuffer.put(value);
					}
				}//无update操作，直接保留
				else {
					newOffsetList.add(new IndexItem(indexItems.get(i).getId(), newDataBuffer.position()));
					newDataBuffer.put(value);
				}
				i++;
			}
			//Id相同，直接替换
			if (log.getId() == indexItems.get(i).getId()) {
				newOffsetList.add(new IndexItem(log.getId(), newDataBuffer.position()));
				newDataBuffer.put(log.getValue());
				i++;
			}
			//log.getId() < indexItems.get(i).getId()时，处理insertLogs中的数据
			else if (log.getId() < indexItems.get(i).getId()) {
				newOffsetList.add(new IndexItem(log.getId(), newDataBuffer.position()));
				newDataBuffer.put(log.getValue());
			}
		}
    	newDataBuffer.flip();
    	return new BlockData(newOffsetList, newDataBuffer);
	}

	/**
	 * 在获取（minId-->maxId之间的数据）data的进程或者actor中调用，以获取data区中的数据
	 * @return BlockData minId-->maxId之间的数据
	 * @throws IllegalBufferMergeException 
	 */
	public BlockData getOriginalDataFromDataRegion() throws IllegalBufferMergeException{
		if (tempBuffer.isReady()) {
			return segmentExcerpt.getDataOperateable().getDataWithIdRange(tempBuffer.getMinId(), tempBuffer.getMaxId());
		}else{
			throw new IllegalBufferMergeException("Method invovation order is error.");
		}
	}
	public TempBuffer getTempBuffer() {
		return tempBuffer;
	}
	public SegmentExcerpt getSegmentExcerpt() {
		return segmentExcerpt;
	}
	/**
	 * 对task中的buffer进行预遍历，获取新的tempbuffer（包含有minId和maxId）
	 * @return
	 */
	public TempBuffer preTraversalOperation(){
		return tempBuffer.preperation();
	}
	
	/**
	 * 将预merge完成的insertlist和updateList与 blockData进行最终的merge，并且写会至data区
	 * 前置条件：预merge工作完成（insertlist和updateList的排序和merge工作）+ 从data区获取原始的minId-->maxId之间的数据
	 * @param blockData
	 */
	public void merge(BlockData blockData){
		BlockData result = mergeOperation(blockData);
		writeBack(result);
	}
}
