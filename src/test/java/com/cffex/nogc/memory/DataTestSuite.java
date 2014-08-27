/**
 *@author  Tao Zhou
*@classname DataTestSuite.java
*@date 下午3:34:29
*@description
 */
package com.cffex.nogc.memory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.cffex.nogc.enumeration.IsolationType;
import com.cffex.nogc.memory.data.BlockData;
import com.cffex.nogc.memory.data.DataExcerpt;
import com.cffex.nogc.memory.data.DataOperateable;
import com.cffex.nogc.memory.data.Index;
import com.cffex.nogc.memory.data.IndexItem;

/**
 * @author zhou
 *
 */
public class DataTestSuite {
	private void insertNewIndex(Object[] newIndex, int offset) {
		// TODO Auto-generated method stub
		byte[] b = new byte[newIndex.length*6];
		System.out.println(newIndex.length);
		byte[] intbyte = new byte[4];
		byte[] longbyte = new byte[8];
		for(int i = 0; i < newIndex.length/2; i++){
			System.out.println(i);
			try {
				longbyte = getBytes(newIndex[i*2]);
				intbyte = getBytes(newIndex[i*2+1]);
				System.arraycopy(longbyte, 0, b, i*12, 4);
				System.arraycopy(intbyte, 0, b, i*12+4, 8);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	private static byte[] getBytes(Object obj) throws IOException {  
	        ByteArrayOutputStream bout = new ByteArrayOutputStream();  
	        ObjectOutputStream out = new ObjectOutputStream(bout);  
	        out.writeObject(obj);  
	        out.flush();  
	        byte[] bytes = bout.toByteArray();  
	        bout.close();  
	        out.close();  
	        System.out.println(bytes.toString());
	        return bytes;  
	} 
	
	//public static void DataInsertTest
	public static void main(String[] args){
//		DataTestSuite dts = new DataTestSuite();
//		SegmentExcerpt se = new SegmentExcerpt(IsolationType.RESTRICT);
//		DataOperateable de = se.getDataOperateable();
//		Object[] testArrayObjects = {(long)1L,(int)11,(long)2L,(int)12,(long)3L,(int)13,(long)4L,(int)14} ;
//		dts.insertNewIndex(testArrayObjects, 0);
		
		//de.insertData(newData, newIndex, miId, maxId, readonly)
		
		SegmentExcerpt segmentExcerpt = new SegmentExcerpt(IsolationType.RESTRICT);
		ByteBuffer dataBuffer = ByteBuffer.allocate(200);
		IndexItem indexItem1 = new IndexItem(1,0);
		IndexItem indexItem2 = new IndexItem(2,10);
		IndexItem indexItem3 = new IndexItem(3,30);
		IndexItem indexItem4 = new IndexItem(4,50);
		IndexItem indexItem5 = new IndexItem(5,100);
		List<IndexItem> offsetList = new ArrayList<IndexItem>();
		offsetList.add(indexItem1);
		offsetList.add(indexItem2);
		offsetList.add(indexItem3);
		offsetList.add(indexItem4);
		offsetList.add(indexItem5);
		BlockData blockData = new BlockData(offsetList, dataBuffer);
		segmentExcerpt.getDataOperateable().insertDataWithIdRange(blockData, 1, 5);
		
		//segmentExcerpt.getItem(id, "");
		
		
	}
}
