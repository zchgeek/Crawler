package com.zoe.crawler.implement;
/**
 * log queue operations 
 * @author zhanglm
 *
 */
public class LogQueue {
	private static Queue logUrl = new Queue();
	/**
	 * remove the element from the log queue and return it
	 * @return object
	 */
	public static synchronized Object deLogQueue() {
		return logUrl.deQueue();
	}
	/**
	 * add an element into a queue
	 * @param log
	 */
	public static synchronized void addLogQueue(String log) {
		if(!logUrl.contains(log)) {
			logUrl.enQueue(log);
		}
	}
	/**
	 * return the number of the elements within the queue
	 * @return
	 */
	public static synchronized int getLogQueueSize() {
		return logUrl.size();
	}
	/**
	 * determining whether the log queue is empty
	 * @return boolean type
	 */
	public static synchronized boolean isLogQueueEmpty() {
		return logUrl.isQueueEmpty();
	}
}
