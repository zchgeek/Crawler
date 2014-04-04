package com.zoe.crawler.implement;

import com.zoe.crawler.model.*;
/**
 * a class using to configure all the parameters initializing
 * the crawling process
 * @author zhanglm
 *
 */
public class CrawlConfig {
//	public String[] urlSeeds = new String[] {"http://finance.sina.com.cn","http://tech.sina.com.cn",
//			"http://news.sina.com.cn","http://mobile.sina.com.cn","http://sports.sina.com.cn/",
//			"http://ent.sina.com.cn/","http://auto.sina.com.cn/","http://edu.sina.com.cn/"};
	/**url seeds from the very beginning*/
	public String[] urlSeeds = new String[] {"http://news.sina.com.cn","http://mobile.sina.com.cn"};
	/**the thread number of crawl process*/
	public int threadCount = 10;
	public CrawlModel cm = new CrawlerImp(threadCount);
	public TimerModel tm = new TimeTask();
	public String logPath = "";
}
