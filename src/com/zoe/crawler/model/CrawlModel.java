package com.zoe.crawler.model;
/**
 * extends this class could plan the overall crawl process
 * @author zhanglm
 *
 */
public abstract class CrawlModel {
	/**the thread number*/
	public int threadCount;
	/**unvisited url queue from the beginning*/
	public String[] urlSeeds;
	/**the regex being used to filter url to being crawled*/
	public String[] regex;
	/**
	 * class constructor
	 * @param threadCount
	 */
	public CrawlModel(int threadCount){
		this.threadCount = threadCount;
	}
	/**
	 * set unvisited url queue from the very beginning
	 * @param urlSeeds string array
	 */
	public abstract void setUrlSeeds(String[] urlSeeds);
	/**
	 * set unvisited regex queue from the very beginning
	 * using urlSeeds
	 */
	public abstract void setRegx();
	/**
	 * initialize crawling seeds
	 */
	public abstract void initCrawlerSeeds();
	/**
	 * definite specific crawling method
	 * @param seeds array of String
	 */
	public abstract void crawling(String[] seeds);
}
