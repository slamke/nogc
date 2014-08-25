/**
 * @time 2014年8月20日
 * @author sunke
 */
package com.cffex.nogc.memory.buffer.merge;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

/**
 * @author sunke
 * @ClassName MergeTaskSender
 * @Description: MergeTaskSender的任务发送器
 */
public class MergeTaskSender {
	
	public static void putTask(MergeTask task) {
		ActorSystem system = ActorSystem.create("NoGc");
		//如果没有启动actor，启动actor
	    ActorRef a = system.actorOf(Props.create(Merger.class), "Merger");
	    //发送任务
	    a.tell(task, null);
	}
}
