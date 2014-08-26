package com.cffex.nogc.memory.buffer.merge;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.pattern.Patterns;
import akka.util.Timeout;

import com.cffex.nogc.memory.data.BlockData;

public class Merger extends UntypedActor {

  private final ActorRef bufferSorter = getContext().actorOf(Props.create(BufferSorter.class), "bufferSorter");
  private final ActorRef dataGetter = getContext().actorOf(Props.create(DataGetter.class), "dataGetter");
  
  @Override
  public void preStart() {
    // //create the greeter actor
    //final ActorRef greeter = getContext().actorOf(Props.create(DataGetter.class), "greeter");
    // //tell it to perform the greeting
    //greeter.tell(DataGetter.Msg.GREET, getSelf());
  }

  @Override
  public void onReceive(Object msg) {
    if (msg instanceof MergeTask) {
    	try {
    		Timeout timeout = new Timeout(Duration.create(1, "seconds"));
    		MergeTask task = (MergeTask)msg;
    		//遍历buffer获取maxId和minId
    		TempBuffer another = task.preTraversalOperation();
    		//mergeTask为不可变类，因此重新构建一个新的对象
    		MergeTask newTask = new MergeTask(another, task.getSegmentExcerpt());
        	//由buffersorter进行buffer中的merge
    		Future<Object> buffer = Patterns.ask(bufferSorter, newTask, timeout);
        	//由dataGetter获取maxId和minId
    		Future<Object> data = Patterns.ask(dataGetter, newTask, timeout);
        	
        	TempBuffer result = (TempBuffer) Await.result(buffer, timeout.duration());
        	BlockData blockData = (BlockData) Await.result(data, timeout.duration());
        	
        	MergeTask thirdTask = new MergeTask(result, task.getSegmentExcerpt());
        	thirdTask.merge(blockData);
        	
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
    } else
      unhandled(msg);
  }
}
