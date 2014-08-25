/**
 * @time 2014年8月22日
 * @author sunke
 */
package com.cffex.nogc.memory.buffer.merge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import akka.actor.UntypedActor;

/**
 * @author sunke
 * @ClassName BufferSorter
 * @Description: TODO 
 */
public class BufferSorter extends UntypedActor {

	  @Override
	  public void onReceive(Object msg) {
	    if (msg instanceof TempBuffer) {
	      System.out.println("BufferSorter start:"+System.currentTimeMillis());
	      try {
			Thread.sleep(20);
	      } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	      }
	      ArrayList<Integer> data = new ArrayList<Integer>(3);
		    data.add(8);
		    data.add(8);
		    data.add(8);
	      getSender().tell(msg, getSelf());
	      System.out.println("BufferSorter end:"+System.currentTimeMillis());
	    } else
	      unhandled(msg);
	  }

}
