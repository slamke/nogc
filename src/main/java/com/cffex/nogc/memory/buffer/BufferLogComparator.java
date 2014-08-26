/**
 * @time 2014年8月25日
 * @author sunke
 */
package com.cffex.nogc.memory.buffer;

import java.util.Comparator;

/**
 * @author sunke
 * @ClassName BufferLogComparator
 * @Description: Tuple<Integer, BufferLog>的比较器，对buffer区进行merge时，需要进行排序时使用 
 */
public class BufferLogComparator implements Comparator<BufferLog> {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(BufferLog o1, BufferLog o2) {
		// TODO Auto-generated method stub
		if (o1.getId() > o2.getId()) {
			return 1;
		}else if (o1.getId() < o2.getId()) {
			return -1;
		}else {
			return 0;
		}
	}
}
