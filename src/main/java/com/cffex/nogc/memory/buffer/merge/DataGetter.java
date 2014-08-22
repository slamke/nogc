package com.cffex.nogc.memory.buffer.merge;

import java.util.ArrayList;
import java.util.Collections;

import akka.actor.UntypedActor;

import com.cffex.nogc.memory.buffer.TempBuffer;

public class DataGetter extends UntypedActor {

  @Override
  public void onReceive(Object msg) {
    if (msg instanceof TempBuffer) {
      System.out.println("DataGetter start:"+System.currentTimeMillis());
      try {
		Thread.sleep(20);
      } catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
      }
      ArrayList<Integer> data = new ArrayList<Integer>(3);
	    data.add(7);
	    data.add(7);
	    data.add(7);
      getSender().tell(Collections.unmodifiableList(data), getSelf());
      System.out.println("DataGetter end:"+System.currentTimeMillis());
    } else
      unhandled(msg);
  }

}
