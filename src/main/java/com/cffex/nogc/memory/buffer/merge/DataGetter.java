package com.cffex.nogc.memory.buffer.merge;

import akka.actor.UntypedActor;

import com.cffex.nogc.memory.buffer.exception.IllegalBufferMergeException;
import com.cffex.nogc.memory.data.BlockData;

public class DataGetter extends UntypedActor {

  @Override
  public void onReceive(Object msg) {
    if (msg instanceof MergeTask) {
		try {
			System.out.println("DataGetter start:"+System.currentTimeMillis());
			MergeTask task = (MergeTask)msg;
	    	BlockData data;
			data = task.getOriginalDataFromDataRegion();
			System.out.println("DataGetter end:"+System.currentTimeMillis());
	    	getSender().tell(data, getSelf());
		} catch (IllegalBufferMergeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    } else
      unhandled(msg);
  	}
}
