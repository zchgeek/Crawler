package com.zoe.crawler.implement;

import java.util.LinkedList;
/**
 * the base class of the LinkQueue that contains many queue operations
 * @author zhanglm
 *
 */
public class Queue {
	private LinkedList<Object> queue = new LinkedList<Object>();
	
	public void enQueue(Object t) {
		queue.addLast(t);
	}
	
	public Object deQueue() {
		return queue.removeFirst();
	}
	
	public boolean isQueueEmpty() {
		return queue.isEmpty();
	}
	
	public boolean contains(Object t) {
		return queue.contains(t);
	}
	
	public int size() {
		return queue.size();
	}
	
	public String toString() {
		return queue.toString();
	}
}
