/**
 * @time 2014年8月26日
 * @author sunke
 */
package com.cffex.nogc.memory.data;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author sunke TaoZhou
 * @ClassName BlockData
 * @Description: 从data区获取数据的block
 */
public class BlockData {
	private final List<IndexItem> offsetList;
	private final ByteBuffer dataBuffer;

	public List<IndexItem> getOffsetList() {
		return offsetList;
	}

	public ByteBuffer getDataBuffer() {
		return dataBuffer;
	}

	/**
	 * @param offsetList
	 * @param dataBuffer
	 */
	public BlockData(List<IndexItem> offsetList, ByteBuffer dataBuffer) {
		super();
		this.offsetList = offsetList;
		this.dataBuffer = dataBuffer;
	}

}
