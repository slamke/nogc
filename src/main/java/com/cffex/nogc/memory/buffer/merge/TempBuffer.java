/**
 * @time 2014年8月20日
 * @author sunke
 */
package com.cffex.nogc.memory.buffer.merge;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cffex.nogc.cson.core.utils.CSONMergeTool;
import com.cffex.nogc.cson.core.utils.Tuple;
import com.cffex.nogc.memory.NoGcByteBuffer;
import com.cffex.nogc.memory.buffer.Buffer;
import com.cffex.nogc.memory.buffer.BufferLog;
import com.cffex.nogc.memory.buffer.BufferLog.BufferLogCusor;
import com.cffex.nogc.memory.buffer.BufferLog.BufferLogType;
import com.cffex.nogc.memory.buffer.BufferLogComparator;
import com.cffex.nogc.memory.buffer.exception.IllegalBufferMergeException;
import com.cffex.nogc.memory.buffer.exception.TempBufferException;

/**
 * @author sunke
 * @ClassName TempBuffer
 * @Description: TempBuffer为buffer的拷贝版本，用于merge
 * tempBuffer为不可变的Pojo-->原因：tempBuffer作为变量在actor模型框架akka中进行传递，actor要求传递的msg为不可变的
 */
public class TempBuffer extends Buffer{
	
	/**
	 *insert bufferLog的list
	 */
	private final List<BufferLog> insertIndexList;
	/**
	 * update/delete bufferLog的list
	 */
	private final List<BufferLog> updateIndexList;
	private final long minId;
	private final long maxId;
	/**
	 * 构建尚未处理的TempBuffer
	 * @param noGcByteBuffer
	 */
	public TempBuffer(NoGcByteBuffer noGcByteBuffer){
		super(noGcByteBuffer);
		this.insertIndexList = null;
		this.updateIndexList = null;
		this.minId = Long.MAX_VALUE;
		this.maxId = Long.MIN_VALUE;
	}
	
	public long getMinId() {
		return minId;
	}



	public long getMaxId() {
		return maxId;
	}
	
	/**构造函数生成的数据是final的，在actor进行传递
	 * @param noGcByteBuffer
	 * @param insertIndexList
	 * @param updateIndexList
	 * @param minId
	 * @param maxId
	 */
	public TempBuffer(List<BufferLog> insertIndexList, List<BufferLog> updateIndexList,
			long minId, long maxId) {
		super(null);
		this.insertIndexList = Collections.unmodifiableList(insertIndexList);
		this.updateIndexList = Collections.unmodifiableList(updateIndexList);
		this.minId = minId;
		this.maxId = maxId;
	}



	/**
	 * 遍历dirctBuffer中的内容，计算最大Id和最小Id，形成未处理的insertIndexList和updateIndexList
	 * @return 新构建的TempBuffer
	 */
	public TempBuffer preperation(){
		BufferLogCusor cusor = new BufferLogCusor(noGcByteBuffer);
		List<BufferLog> unhandledInsert = new ArrayList<BufferLog>();
		List<BufferLog> unhandledUpdate = new ArrayList<BufferLog>();
		long maxId = Long.MIN_VALUE;
		long minId = Long.MAX_VALUE;
		while (cusor.hasNext()) {
			BufferLog log = cusor.next();
			if (log.getFlag() == BufferLogType.INSERT) {
				unhandledInsert.add(log);
			} else {
				unhandledUpdate.add(log);
			}
			minId = Math.min(minId, log.getId());
			maxId = Math.max(maxId, log.getId());
		}
		return new TempBuffer(unhandledInsert, unhandledUpdate, minId, maxId);
	}
	
	/**
	 * 对未处理的insertIndexList和updateIndexList进行排序和归并处理
	 * @throws TempBufferException 
	 */
	public TempBuffer constructIndexList() throws TempBufferException{
		if (!isReady()) {
			throw new TempBufferException("Invoke the preperation() method first and get the return value to invoke this method.");
		}
		//id-->所有的insert操作，便于后期与update的操作进行merge
		Map<Long,List<BufferLog>> insertMap = new HashMap<Long,List<BufferLog>>();
		if (insertIndexList != null && insertIndexList.size() > 0) {
			//根据Id对insertIndexList进行排序-->底层实现为归并排序，为稳定排序，所以insertlist排序后，同一个id的operation依然保持时间上的先后顺序
			Collections.sort(insertIndexList,new BufferLogComparator());
			//构建map
			for (BufferLog log : insertIndexList) {
				if (insertMap.get(log.getId()) == null) {
					List<BufferLog> list = new ArrayList<BufferLog>();
					list.add(log);
					insertMap.put(log.getId(), list);
				}else {
					insertMap.get(log.getId()).add(log);
				}
			}
		}
		//尽可能将update的operation merge到现有的insert operation上，无法merge的operation为remainingUpdateBufferLogs
		List<BufferLog> remainingUpdateBufferLogs = new ArrayList<BufferLog>(); 
		if (updateIndexList != null && updateIndexList.size()>0) {
			//根据Id和order对updateIndexList进行排序-->归并排序
			Collections.sort(updateIndexList,new BufferLogComparator());
			int i = 0;
			while (i < updateIndexList.size()) {
				BufferLog current  = updateIndexList.get(i);
				//update log有对应的insert operation，则进行merge
				if (insertMap.containsKey(current.getId())) {
					List<BufferLog> subUpdateList = new ArrayList<BufferLog>();
					subUpdateList.add(current);
					i++;
					//获取同一个Id的所有update logs
					while (i < updateIndexList.size() && 
							updateIndexList.get(i).getId() == subUpdateList.get(0).getId()) {
						subUpdateList.add(updateIndexList.get(i));
						i++;
					}
					//将update的operation merge到现有的insert operation上
					List<BufferLog>  res =  mergeBufferLog(insertMap.get(current.getId()), subUpdateList);
					//merge完成后，放入insertMap
					if (res == null || res.size() == 0) {
						insertMap.remove(current.getId());
					}else {
						insertMap.put(current.getId(), res);
					}
				}
				//update log没有对应的insert operation，则保留至remainingUpdateBufferLogs
				else {
					remainingUpdateBufferLogs.add(current);
				}
			}
		}
		//update和insert构建完成后，创建新的TempBuffer
		List<BufferLog>  remainingInsertBufferLogs = new ArrayList<BufferLog>();
		for (List<BufferLog> list : insertMap.values()) {
			remainingInsertBufferLogs.addAll(list);
		}
		return new TempBuffer(remainingInsertBufferLogs, remainingUpdateBufferLogs, this.minId, this.maxId);
	}
	/**
	 * 判断该tempbuffer是否经过了预遍历处理
	 * @return 结果标识
	 */
	public boolean isReady(){
		//经过preperation()函数处理后，maxId和minID均不能保持原值
		if (maxId == Long.MIN_VALUE || minId == Long.MAX_VALUE ) {
			return false;
		}else {
			return true;
		}
	}
	private List<BufferLog> mergeBufferLog(List<BufferLog> insertList, List<BufferLog> updateList) throws IllegalBufferMergeException{
		if (updateList != null) {
			for (BufferLog bufferLog : updateList) {
				if (insertList.size() == 0) {
					throw new IllegalBufferMergeException("Illegal update/delete when there is no insert operation.");
				}
				//因为insertList和updateList进行排序后，所有的时间顺序依然保持，所以只需要对insertList的首元素进行merge操作
				//如果update是delete操作，删除insertList中首位的记录
				if (bufferLog.getFlag() == BufferLogType.DELETE) {
					insertList.remove(0);
				}
				//如果update是UPDATE_ALL操作，替换insertList中首位的元素的内容
				else if (bufferLog.getFlag() == BufferLogType.UPDATE_ALL) {
					BufferLog insert = insertList.get(0);
					insert.setValue(bufferLog.getValue());
				}
				//如果update是UPDATE_PROPERTY操作，更新insertList中首位的元素的内容
				else if (bufferLog.getFlag() == BufferLogType.UPDATE_PROPERTY) {
					try {
						BufferLog insert = insertList.get(0);
						ByteBuffer bb = ByteBuffer.wrap(insert.getValue()).order(ByteOrder.LITTLE_ENDIAN);
						ByteBuffer content = CSONMergeTool.merge(Class.forName(bufferLog.getSchemaKey()), bb, new Tuple<Integer, byte[]>(bufferLog.getIndex(), bufferLog.getValue()));
						insert.setValue(content.get);
					} catch (ClassNotFoundException e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}
			}
		}
		return null;
	}
}
