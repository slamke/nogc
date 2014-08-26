/**
 * @time 2014年8月22日
 * @author sunke
 */
package com.cffex.nogc.memory.buffer.merge;

import akka.actor.UntypedActor;

import com.cffex.nogc.memory.buffer.exception.IllegalBufferMergeException;
import com.cffex.nogc.memory.buffer.exception.TempBufferException;

/**
 * @author sunke
 * @ClassName BufferSorter
 * @Description: TODO 
 */
public class BufferSorter extends UntypedActor {

	  @Override
	  public void onReceive(Object msg) {
	    if (msg instanceof MergeTask) {
	      System.out.println("BufferSorter start:"+System.currentTimeMillis());
	      try {
			MergeTask task = (MergeTask)msg;
			//对tempBuffer中的数据进行初期的merge操作。
			TempBuffer buffer = task.getTempBuffer().constructIndexList();
			getSender().tell(buffer, getSelf());
	      } catch (TempBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	      } catch (IllegalBufferMergeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	      }
	      System.out.println("BufferSorter end:"+System.currentTimeMillis());
	    } else
	      unhandled(msg);
	  }

}
