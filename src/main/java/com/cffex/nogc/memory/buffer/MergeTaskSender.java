/**
 * @time 2014年8月20日
 * @author sunke
 */
package com.cffex.nogc.memory.buffer;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import com.cffex.nogc.memory.buffer.merge.Merger;

/**
 * @author sunke
 * @ClassName MergeTaskSender
 * @Description: MergeTaskSender的任务发送器
 */
public class MergeTaskSender {
	
	public static void putTask(MergeTask task) {
		ActorSystem system = ActorSystem.create("NoGc");
	    ActorRef a = system.actorOf(Props.create(Merger.class), "Merger");
	    a.tell(task, null);
	}
}
