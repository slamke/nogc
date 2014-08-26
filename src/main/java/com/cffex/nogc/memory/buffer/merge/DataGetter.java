package com.cffex.nogc.memory.buffer.merge;

import akka.actor.UntypedActor;

import com.cffex.nogc.memory.data.BlockData;
import com.cffex.nogc.memory.data.DataOperateable;

public class DataGetter extends UntypedActor {

  @Override
  public void onReceive(Object msg) {
    if (msg instanceof MergeTask) {
    	System.out.println("DataGetter start:"+System.currentTimeMillis());
    	MergeTask task = (MergeTask)msg;
    	DataOperateable operateable = task.getSegmentExcerpt().getDataOperateable();
    	BlockData data = operateable.getDataWithIdRange(task.getTempBuffer().getMinId(), task.getTempBuffer().getMaxId());
    	System.out.println("DataGetter end:"+System.currentTimeMillis());
    	getSender().tell(data, getSelf());
    } else
      unhandled(msg);
  	}
}
