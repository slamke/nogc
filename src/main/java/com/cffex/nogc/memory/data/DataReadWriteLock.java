/**
 *@author  Tao Zhou
*@classname DataReadWriteLock.java
*@date 上午10:19:21
*@description
 */
package com.cffex.nogc.memory.data;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.cffex.nogc.memory.data.exception.DataException;

/**
 * @author zhou
 *
 */
public class DataReadWriteLock {

	private Map<Thread, Integer> readingThreads = new HashMap<Thread, Integer>();
	private int writeAccesses    = 0;
	private int writeRequests    = 0;
	private Thread writingThread = null;
	/**
	 * 原子boolean类型，用来实现spinlock
	 */
	private AtomicBoolean lock;
	
	/**
	 * spin lock的time_out时间，尝试10次
	 */
	private final int SPIN_LOCK_TIME_OUT = 10;
	
	/**
	 * spin lock尝试时的sleep时间
	 */
	private final int SPIN_LOCK_SLEEP_TIME = 20;
	protected boolean SpinlockRead(){
		int time = 0;
		while (!lock.compareAndSet(false, true) && time<SPIN_LOCK_TIME_OUT){
			try {
				Thread.sleep(SPIN_LOCK_SLEEP_TIME);	
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			time++;
		}
		if (time<SPIN_LOCK_TIME_OUT && lock.get()== true) {
			return true;
		}else {
			return false;
		}
	}
	
	protected void unSpinlockRead(){
		lock.compareAndSet(true, false);
	}

	public synchronized void lockRead()
		throws InterruptedException, DataException{
		Thread callingThread = Thread.currentThread();
		while(! canGrantReadAccess(callingThread)){
			wait();
		}

		readingThreads.put(callingThread,
			(getReadAccessCount(callingThread) + 1));
	}

	private boolean canGrantReadAccess(Thread callingThread){
		if(isWriter(callingThread)) return true;
		if(hasWriter()) return false;
		if(isReader(callingThread)) return true;
		if(hasWriteRequests()) return false;
		return true;
	}


	public synchronized void unlockRead(){
		Thread callingThread = Thread.currentThread();
		if(!isReader(callingThread)){
			throw new IllegalMonitorStateException(
				"Calling Thread does not" +
				" hold a read lock on this ReadWriteLock");
		}
		int accessCount = getReadAccessCount(callingThread);
		if(accessCount == 1){ 
			readingThreads.remove(callingThread); 
		} else { 
			readingThreads.put(callingThread, (accessCount -1));
		}
		notifyAll();
	}

	public synchronized void lockWrite() 
		throws InterruptedException{
		Thread callingThread = Thread.currentThread();
		writeRequests++;
		while(!canGrantWriteAccess(callingThread)){
			wait();
		}
		writeRequests--;
		writeAccesses++;
		writingThread = callingThread;
		writingThread.start();
	}

	public synchronized void unlockWrite() 
		throws InterruptedException{
		Thread callingThread = Thread.currentThread();
		if(!isWriter(callingThread)){
		throw new IllegalMonitorStateException(
			"Calling Thread does not" +
			" hold the write lock on this ReadWriteLock");
		}
		writeAccesses--;
		if(writeAccesses == 0){
			writingThread = null;
		}
		notifyAll();
	}

	private boolean canGrantWriteAccess(Thread callingThread){
		if(isOnlyReader(callingThread)) return true;
		if(hasReaders()) return false;
		if(writingThread == null) return true;
		if(!isWriter(callingThread)) return false;
		return true;
	}


	private int getReadAccessCount(Thread callingThread){
		Integer accessCount = readingThreads.get(callingThread);
		if(accessCount == null) return 0;
		return accessCount.intValue();
	}


	private boolean hasReaders(){
		return readingThreads.size() > 0;
	}

	private boolean isReader(Thread callingThread){
		return readingThreads.get(callingThread) != null;
	}

	private boolean isOnlyReader(Thread callingThread){
		return readingThreads.size() == 1 &&
			readingThreads.get(callingThread) != null;
	}

	private boolean hasWriter(){
		return writingThread != null;
	}

	private boolean isWriter(Thread callingThread){
		return writingThread == callingThread;
	}

	private boolean hasWriteRequests(){
		return this.writeRequests > 0;
	}
}
