/**
 * @time 2014年8月22日
 * @author sunke
 */
package com.cffex.nogc.memory;

import java.util.ArrayList;
import java.util.Collections;

import scala.concurrent.ExecutionContext;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.dispatch.ExecutionContexts;

import com.cffex.nogc.memory.buffer.merge.MergeTask;
import com.cffex.nogc.memory.buffer.merge.Merger;
import com.cffex.nogc.memory.buffer.merge.TempBuffer;

/**
 * @author sunke
 * @ClassName MergeTestSuite
 * @Description: TODO 
 */
public class MergeTestSuite {
	public static void main(String[] args) {
	    //akka.Main.main(new String[] { Merger.class.getName() });
	    ActorSystem system = ActorSystem.create("Hello");
	    final ExecutionContext ec = ExecutionContexts.global();
	    final ActorRef merger = system.actorOf(Props.create(Merger.class), "Merger");
	    // tell it to perform the greeting
	    merger.tell(new MergeTask(new TempBuffer(null), null), null);
	  }
}
