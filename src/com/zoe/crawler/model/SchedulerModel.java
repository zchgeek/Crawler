package com.zoe.crawler.model;
/**
 * the main schedule class being used to do global operations
 * @author zhanglm
 *
 */
public abstract class SchedulerModel {
	/**an array of url seeds*/
	public String[] urlSeeds;
	/**a CrawlModel type parameter used to crawl pages*/
	public CrawlModel cm;
	/**a TimerModel type parameter used to set time task*/
	public TimerModel tm;
	/**
	 * initializing unvisited url queue
	 * @param urlSeeds
	 */
	abstract public void initSeeds(String[] urlSeeds);
	/**
	 * override this abstract function could customize 
	 * it's own website crawl method
	 * @param cm a CrawModel type
	 */
	abstract public void crawlProcess(CrawlModel cm);
	/**
	 * override this abstract function could customize 
	 * it's own time task schedule
	 * @param tm a TimerModel type
	 */
	abstract public void setTimer(TimerModel tm);
	
}
