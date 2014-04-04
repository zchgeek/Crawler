package com.zoe.crawler.model;
/**
 * the thread class of crawling website
 * @author zhanglm
 *
 */
public abstract class CrawlThreadModel extends Thread{
	/**
	 * implements run() function
	 */
	public abstract void run();
	/**
	 * set filter method being used to filter visited urls
	 * @param visitedUrl
	 */
	public abstract void setFilterMethod(String visitedUrl);
	/**
	 * definite recheck method 
	 * @param url
	 * @return
	 */
	public abstract boolean setRecheckMethod(String url);
	/**
	 * some operations with log
	 */
	public abstract void logOperation();
	/**
	 * definite method that could download needed content from url
	 */
	public abstract String downloadProcess(String url);
}
