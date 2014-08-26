package com.cffex.nogc.memory.data;
/**
 * @author Tao Zhou
 * @ClassName DataExcerpt
 * @Description: Data操作接口
 */
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.swing.text.DefaultEditorKit.CopyAction;

import sun.nio.ch.DirectBuffer;

import com.cffex.nogc.enumeration.IsolationType;
import com.cffex.nogc.memory.NoGcByteBuffer;
import com.cffex.nogc.memory.Segment;
import com.cffex.nogc.memory.SegmentExcerpt;
import com.cffex.nogc.memory.SegmentOperateable;
import com.cffex.nogc.memory.buffer.Buffer;
import com.cffex.nogc.memory.data.exception.DataException;
import com.cffex.nogc.memory.utils.MemoryTool;
import com.sun.beans.editors.ByteEditor;

public class DataExcerpt extends AbstractDataExcerpt{

	//private region
	
	
	public DataExcerpt(SegmentExcerpt segmentExcerpt, Data data){
		super(segmentExcerpt, data);

	}
	public DataExcerpt(SegmentExcerpt segmentExcerpt, NoGcByteBuffer nogcData){
		super(segmentExcerpt, nogcData);

	}
	public Data getData() {
		return data;
	}
	//private methods to implement functions in DataOperateable
	
	
	
	
	

	protected byte[] getDataById(long id) {
		// TODO Auto-generated method stub
		int offset = getOffsetById(id);
		if(offset < 0){
			return null;
		}else{
			return this.data.getDataByOffset(offset);
		}
	}
	

	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.data.AbstractDataExcerpt#getDataPropertyById(long, int)
	 */
	//未完成，需要结合cson实现，找到object的第几个属性和offset的对应关系
	@Override
	protected byte[] getDataPropertyById(long id, int index) {
		// TODO Auto-generated method stub
		int offset = getOffsetById(id);
		if(offset < 0){
			return null;
		}else{
			//todo
			return this.data.getDataByOffset(offset);
		}
	}
	
	/*
	 * 在Index区二分查找
	 * @param id 查找的id
	 * @return offset的值
	 */
	protected int getOffsetById(long id) {
		int offset = binarySearchById(id);
		if(offset < 0){
			return -1;
		}else{
			return this.data.getDataItemStartOffset(offset);
		}
		
	}

	
	

	
	
	
	//object数组id,index,id,index
	public List<IndexItem> getIndexRegion(long minId, long maxId){
		int minIdIndexOffset = findIdOffset(minId);//找到最小id的index offset
		int maxIdIndexOffset = findIdOffset(maxId);//找到最大id的index offset
		int minIdDataStartOffset = this.data.getDataItemStartOffset(minIdIndexOffset);//最小id的data offset
		return this.data.getIndex().getOffsetList(minIdIndexOffset, maxIdIndexOffset, minIdDataStartOffset);
	}
	protected int setIndexRegion(){
		return 0;
	}

	protected int checkFreeSpace(){
		if(data.getFreesapce() < Data.THRESHOLD){
			return 0;
		}else{
			return 1;
		}
	}
	
	protected SegmentExcerpt resizeData(){
		
		int newCapacity = (int) (data.getCapacity()*Segment.DEFAULT_REACTOR);
		int newFreeSpace = newCapacity - (this.data.getCapacity()-this.data.getFreesapce());
		Segment newSegment = new Segment(newCapacity);
		SegmentExcerpt newsegmentExcerpt = new SegmentExcerpt(IsolationType.RESTRICT);
		NoGcByteBuffer newNoGcData = new NoGcByteBuffer(Buffer.CAPACITY, newCapacity, newsegmentExcerpt.getSegment().getByteBuffer());
		
		Data newData = new Data(newCapacity, newFreeSpace, this.data.getMaxId(), this.data.getMinId(), this.data.getCount(),newNoGcData);
		DataExcerpt newDataExcerpt = new DataExcerpt(newsegmentExcerpt,newData);
		
		int segmentStartOffset = 0;
		
		int indexStartOffset = this.data.getIndexStartOffset();
		int indexEndOffset = this.data.getIndexEndOffset();
		int indexLength = indexEndOffset - indexStartOffset;
		
		int dataStartOffset = this.data.getDataStartOffset();
		int dataEndOffset = this.data.getDataEndOffset();
		int bufferAndDataLength = dataEndOffset - dataStartOffset;
		
		int newSegmentStartOffset = 0;
		int newIndexEndOffset = newData.getIndexEndOffset();
		int newIndexStartOffset = newIndexEndOffset - indexLength;

		return newsegmentExcerpt;
	}
	
	/**
	 * @return
	 */

	//更新index中的offset值
	private byte[] updateIndex(byte[] index, int addoffset){
		return this.data.getIndex().update(index ,addoffset);
		
		
	}
	private int updateIndexData(byte[] index, int addoffset){
		return 0;
	}
	
	
	

	




	



	@Override
	protected byte[] getData0(long minId, long maxId) {
		// TODO Auto-generated method stub
		int minIdIndexOffset = findIdOffset(minId);//找到最小id的index offset
		int maxIdIndexOffset = findIdOffset(maxId);//找到最大id的index offset
		int minIdDataStartOffset = this.data.getDataItemStartOffset(minIdIndexOffset);//最小id的data offset
		int maxIdDataStartOffset = this.data.getDataItemStartOffset(maxIdIndexOffset);//最大id的data offset
		//最后一个data结束的位置 = data offset+cson length(4) + length
		
		int maxIdDataEndOffset = this.data.getDataItemEndOffset(maxIdDataStartOffset);
		byte[] result = this.data.getDatas(minIdDataStartOffset, maxIdDataEndOffset);
		return result;
	}
	
	@Override
	protected BlockData getData(long minId, long maxId) {
		// TODO Auto-generated method stub
		int minIdIndexOffset = findIdOffset(minId);//找到最小id的index offset
		int maxIdIndexOffset = findIdOffset(maxId);//找到最大id的index offset
		int minIdDataStartOffset = this.data.getDataItemStartOffset(minIdIndexOffset);//最小id的data offset
		int maxIdDataStartOffset = this.data.getDataItemStartOffset(maxIdIndexOffset);//最大id的data offset
		//最后一个data结束的位置
		int maxIdDataEndOffset = this.data.getDataItemEndOffset(maxIdDataStartOffset);
		byte[] dataBytes = this.data.getDatas(minIdDataStartOffset, maxIdDataEndOffset);
		ByteBuffer buf = ByteBuffer.allocate(dataBytes.length);
		buf.put(dataBytes);
		buf.flip();
		List<IndexItem> offsetList = this.data.getIndex().getOffsetList(minIdIndexOffset, maxIdIndexOffset,minIdDataStartOffset);
		
		BlockData result = new BlockData(offsetList, buf);
		return result;
	}
	
	@Override
	protected void insertData(byte[] dataBytes, byte[] indexBytes, long minId, long maxId) {
		// TODO Auto-generated method stub
		if(indexBytes == null||dataBytes==null){
			try {
				throw new DataException("datas in index array is not enough!!!");
			} catch (DataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(minId<this.data.getMinId()){
			this.data.setMinId(minId);
		}
		if(maxId > this.data.getMaxId()){
			this.data.setMaxId(maxId);
		}

		int maxIdIndexOffset = findIdOffset(maxId);
		int minIdIndexOffset = findIdOffset(minId);
		int minIdDataStartOffset = this.data.getDataItemStartOffset(minIdIndexOffset);//最小id的data offset
		int maxIdDataStartOffset = this.data.getDataItemStartOffset(maxIdIndexOffset);//最大id的data offset
		//大于maxid的data数据移动量
		int dataOffsetIncrement = dataBytes.length - (maxIdDataStartOffset - minIdDataStartOffset);
		//index区数据移动
		copyLargerThanMaxIdIndex(maxIdIndexOffset, minIdIndexOffset, indexBytes.length, dataOffsetIncrement);
		//data区数据移动
		copyLargerThanMaxIdData(maxIdIndexOffset, dataBytes.length, dataOffsetIncrement);
		//data区插入
		insertData(dataBytes, minIdIndexOffset);
		//index区插入
		insertIndex(indexBytes, minIdDataStartOffset, minIdIndexOffset);
	}
	/**
	 * @param indexBytes
	 * @param minIdDataOffset
	 * @param minIdIndexOffset
	 */
	private void insertIndex(byte[] indexBytes, int minIdDataOffset,
			int minIdIndexOffset) {

		this.data.getIndex().insertIndex(indexBytes, minIdDataOffset, minIdIndexOffset);
		
		
		// TODO Auto-generated method stub
		
	}
	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.data.AbstractDataExcerpt#insertData(byte[], com.cffex.nogc.memory.data.IndexItem[], long, long)
	 */
	@Override
	protected void insertData(byte[] dataBytes, IndexItem[] indexItems,
			long minId, long maxId) {
		if(indexItems.length<1){
			try {
				throw new DataException("datas in index array is not enough!!!");
			} catch (DataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(minId<this.data.getMinId()){
			this.data.setMinId(minId);
		}
		if(maxId > this.data.getMaxId()){
			this.data.setMaxId(maxId);
		}

		int maxIdIndexOffset = findIdOffset(maxId);
		int minIdIndexOffset = findIdOffset(minId);
		int minIdDataStartOffset = this.data.getDataItemStartOffset(minIdIndexOffset);//最小id的data offset
		int maxIdDataStartOffset = this.data.getDataItemStartOffset(maxIdIndexOffset);//最大id的data offset
		//大于maxid的data数据移动量
		int dataOffsetIncrement = dataBytes.length - (maxIdDataStartOffset - minIdDataStartOffset);
		//index区数据移动
		copyLargerThanMaxIdIndex(maxIdIndexOffset, minIdIndexOffset, indexItems.length*Index.INDEX_ITEM_LENGTH, dataOffsetIncrement);
		//data区数据移动
		copyLargerThanMaxIdData(maxIdIndexOffset, dataBytes.length, dataOffsetIncrement);
		//data区插入
		insertData(dataBytes, minIdIndexOffset);
		//index区插入
		insertIndex(indexItems, minIdDataStartOffset, minIdIndexOffset);
	}
	

	/**
	 * @param indexItems
	 * @param minIdDataOffset
	 * @param minIdIndexOffset
	 */
	private void insertIndex(IndexItem[] indexItems, int minIdDataOffset,
			int minIdIndexOffset) {
		this.data.getIndex().insertIndex(indexItems, minIdDataOffset, minIdIndexOffset);
	}
	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.data.AbstractDataExcerpt#insertData(com.cffex.nogc.memory.data.BlockData, long, long)
	 */
	@Override
	protected void insertData(BlockData blockData, long minId, long maxId) {
		// TODO Auto-generated method stub
		if(blockData.getOffsetList().size()<1){
			try {
				throw new DataException("datas in index array is not enough!!!");
			} catch (DataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(minId<this.data.getMinId()){
			this.data.setMinId(minId);
		}
		if(maxId > this.data.getMaxId()){
			this.data.setMaxId(maxId);
		}

		int maxIdIndexOffset = findIdOffset(maxId);
		int minIdIndexOffset = findIdOffset(minId);
		int minIdDataStartOffset = this.data.getDataItemStartOffset(minIdIndexOffset);//最小id的data offset
		int maxIdDataStartOffset = this.data.getDataItemStartOffset(maxIdIndexOffset);//最大id的data offset
		//大于maxid的data数据移动量
		int dataOffsetIncrement = blockData.getDataBuffer().capacity() - (maxIdDataStartOffset - minIdDataStartOffset);
		//index区数据移动
		copyLargerThanMaxIdIndex(maxIdIndexOffset, minIdIndexOffset, blockData.getOffsetList().size()*Index.INDEX_ITEM_LENGTH, dataOffsetIncrement);
		//data区数据移动
		copyLargerThanMaxIdData(maxIdIndexOffset, blockData.getDataBuffer().capacity(), dataOffsetIncrement);
		//data区插入
		insertData(blockData.getDataBuffer().array(), minIdIndexOffset);
		//index区插入
		insertIndex(blockData.getOffsetList(), minIdDataStartOffset, minIdIndexOffset);
	}
	private void insertIndex(List<IndexItem> offstList, int minIdDataOffset,
			int minIdIndexOffset) {
		this.data.getIndex().insertIndex(offstList, minIdDataOffset, minIdIndexOffset);
	}
	
	/**
	 * @param dataBytes
	 * @param minIdOffsetIndex
	 */
	private void insertData(byte[] dataBytes, int minIdOffsetIndex) {
		// TODO Auto-generated method stub
		int minIdOffsetData = this.data.getDataOffsetByIndexOffset(minIdOffsetIndex);

		this.data.putBytes(dataBytes, minIdOffsetData);
		
	}
	/**
	 * @param maxIdOffsetIndex
	 * @param minIdOffsetIndex
	 * @param dataOffsetIncrement 
	 * @param length
	 */
	private void copyLargerThanMaxIdData(int maxIdOffsetIndex, int dataLength, int dataOffsetIncrement) {
		this.data.copyLargerThanMaxIdData(maxIdOffsetIndex, dataLength, dataOffsetIncrement);
		
	}
	/**
	 * @param newIndex
	 */


	/**
	 * @param dataOffsetIncrement 
	 * @param maxId
	 */
	private void copyLargerThanMaxIdIndex(int maxIdOffsetIndex, int minIdOffsetIndex, int indexLength, int dataOffsetIncrement) {
		// TODO Auto-generated method stub
		this.data.getIndex().copyLargerThanMaxIdIndex(maxIdOffsetIndex, minIdOffsetIndex, indexLength, dataOffsetIncrement);
	}
	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.data.AbstractDataExcerpt#binarySearchById(long)
	 */
	@Override
	protected int binarySearchById(long id) {
		// TODO Auto-generated method stub
		return this.data.getIndex().binarySearchById(id);
	}
	/* (non-Javadoc)
	 * @see com.cffex.nogc.memory.data.AbstractDataExcerpt#findIdOffset(long)
	 */
	@Override
	protected int findIdOffset(long id) {
		// TODO Auto-generated method stub
		return this.data.getIndex().findIdOffset(id);
	}
//===============================





	



	
	

}
