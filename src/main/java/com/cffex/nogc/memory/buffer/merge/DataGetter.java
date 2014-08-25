package com.cffex.nogc.memory.buffer.merge;

import java.util.ArrayList;
import java.util.Collections;

import akka.actor.UntypedActor;

public class DataGetter extends UntypedActor {

  @Override
  public void onReceive(Object msg) {
    if (msg instanceof MergeTask) {
    	System.out.println("DataGetter start:"+System.currentTimeMillis());
    	MergeTask task = (MergeTask)msg;
    	//task.
    	System.out.println("DataGetter end:"+System.currentTimeMillis());
    	//getSender().tell(Collections.unmodifiableList(data), getSelf());
    	
    } else
      unhandled(msg);
  	}
}
