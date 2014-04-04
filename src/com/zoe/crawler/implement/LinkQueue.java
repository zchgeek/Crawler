package com.zoe.crawler.implement;

import java.util.HashSet;
import java.util.Set;
/**
 * this class contains many operations about queue that mainly used to 
 * save unvisited urls
 * @author zhanglm
 *
 */
public class LinkQueue {
	private static Set<Object> visitedURL = new HashSet<Object>();
	private static Queue unvisitedURL = new Queue();
	public static int visitedNum = 0;
	/**
	 * get all unvisited urls 
	 * @return a queue
	 */
	public synchronized static Queue getUnvisitedURL() {
		return unvisitedURL;
	}
	/**
	 * add a visited url to the existing queue
	 * @param url
	 */
	public synchronized static void addVisitedURL(String url) {
		visitedURL.add(url);
	}
	/**
	 * remove an url from visited url queue and return none
	 * @param url
	 */
	public synchronized static void removeVisitedURL(String url) {
		visitedURL.remove(url);
	}
	/**
	 * remove an url from visited url queue and return this element
	 * @return element
	 */
	public synchronized static Object unvisitedURLDeQueue() {
		return unvisitedURL.deQueue();
	}
	/**
	 * add a unvisited url to the existing queue
	 * @param url
	 */
	public synchronized static void addUnvisitedURL(String url) {
		if(url!=null && !url.trim().equals("") 
				&& !visitedURL.contains(url) && !unvisitedURL.contains(url)){
			unvisitedURL.enQueue(url);
		}
	}
	/**
	 * return the number of visited url queue size
	 * @return int type
	 */
	public synchronized static int getVisitedURLNum() {
		return visitedURL.size();
	}
	/**
	 * return the number of unvisited url queue size
	 * @return int type
	 */
	public synchronized static int unvisitedURLNum() {
		return unvisitedURL.size();
	}
	/**
	 * return whether the unvisited url queue is empty
	 * if empty return true,else return false
	 * @return boolean type
	 */
	public synchronized static boolean unvisitedURLsEmpty() {
		return unvisitedURL.isQueueEmpty();
	}
}
