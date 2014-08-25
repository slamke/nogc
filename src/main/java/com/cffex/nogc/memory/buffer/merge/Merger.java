package com.cffex.nogc.memory.buffer.merge;

import java.util.List;

import com.cffex.nogc.memory.buffer.MergeTask;
import com.cffex.nogc.memory.buffer.TempBuffer;
import com.sun.scenario.effect.Merge;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.pattern.Patterns;
import akka.util.Timeout;

public class Merger extends UntypedActor {

  private final ActorRef bufferSorter = getContext().actorOf(Props.create(BufferSorter.class), "bufferSorter");
  private final ActorRef dataGetter = getContext().actorOf(Props.create(DataGetter.class), "dataGetter");
  
  @Override
  public void preStart() {
    // create the greeter actor
    //final ActorRef greeter = getContext().actorOf(Props.create(DataGetter.class), "greeter");
    // tell it to perform the greeting
    //greeter.tell(DataGetter.Msg.GREET, getSelf());
  }

  @Override
  public void onReceive(Object msg) {
    if (msg instanceof MergeTask) {
    	try {
    		Timeout timeout = new Timeout(Duration.create(1, "seconds"));
    		MergeTask task = (MergeTask)msg;
    		TempBuffer another = task.merge();
        	Future<Object> buffer = Patterns.ask(bufferSorter, another, timeout);
        	Future<Object> data = Patterns.ask(dataGetter, another, timeout);
        	TempBuffer result = (TempBuffer) Await.result(buffer, timeout.duration());
        	List<Integer> result2 = (List<Integer>) Await.result(data, timeout.duration());
        	System.out.println("buffer:"+result.toString());
        	System.out.println("data:"+result2.toString());
        	System.out.println("merging");
        	getSender().tell("merging", getSelf());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
    } else
      unhandled(msg);
  }
}
